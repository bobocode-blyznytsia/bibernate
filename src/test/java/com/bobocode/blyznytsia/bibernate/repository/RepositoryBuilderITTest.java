package com.bobocode.blyznytsia.bibernate.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import com.bobocode.blyznytsia.bibernate.exception.RepositoryException;
import com.bobocode.blyznytsia.bibernate.session.Session;
import com.bobocode.blyznytsia.bibernate.session.SessionFactory;
import com.bobocode.blyznytsia.bibernate.session.SessionImpl;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseNameGenerator.class)
class RepositoryBuilderITTest {
  private static final String BAD_METHOD_NAMING_OR_RETURN_EXCEPTION_TEXT = "Invalid method declaration: "
      + "make sure the method name align with convention and the return type is supported";
  private static final String COUNT_OF_PARAM_DOES_NOT_MATCH_EXCEPTION_TEXT = "Count of method parameters " +
      "does not match count method arguments. Expected: 2, actual: 1";

  private static Connection connection;
  private  Session session;
  @Mock
  private  SessionFactory sessionFactory;
  private  SampleRepository generatedRepo;

  @BeforeAll
  static void getConnectionWithInitData() throws SQLException {
    connection = DriverManager.getConnection(
        "jdbc:h2:mem:~/test;" +
            "MODE=PostgreSQL;" +
            "INIT=runscript from './src/test/resources/sql/init.sql'");
  }

  @BeforeEach
  void getSessionWithInitData() {
    session = new SessionImpl(connection, sessionToBeClosed -> System.out.println("session closed!"));
    var repositoryBuilder = new RepositoryBuilder(sessionFactory);
    generatedRepo = repositoryBuilder.buildRepository(SampleRepository.class, SampleEntity.class);
  }

  @Test
  void generatesMethodProperly() {
    when(sessionFactory.openSession()).thenReturn(session);
    var expectedEntity = new SampleEntity(3L, "val2");
    var actualEntityOptional = generatedRepo.findOneById(3L);
    assertTrue(actualEntityOptional.isPresent());
    assertEquals(expectedEntity, actualEntityOptional.get());

  }

  @Nested
  class ThrowsRepositoryException {
    @Test
    void whenMethodPoorlyNamed() {
      var ex = assertThrows(RepositoryException.class, () -> generatedRepo.poorlyNamedMethod());
      assertEquals(BAD_METHOD_NAMING_OR_RETURN_EXCEPTION_TEXT, ex.getMessage());
    }

    @Test
    void whenParamCountDoesNotMatches() {
      when(sessionFactory.openSession()).thenReturn(session);
      var ex = assertThrows(RepositoryException.class, () -> generatedRepo.findAllByIdAndSomeValue(1L));
      assertEquals(COUNT_OF_PARAM_DOES_NOT_MATCH_EXCEPTION_TEXT, ex.getMessage());
    }

    @Test
    void whenReturnTypeIsNotSupported() {
      var ex = assertThrows(RepositoryException.class, () -> generatedRepo.findAllById(1L));
      assertEquals(BAD_METHOD_NAMING_OR_RETURN_EXCEPTION_TEXT, ex.getMessage());
    }
  }


  private interface SampleRepository extends BibernateReadonlyRepository<SampleEntity, Long> {
    List<SampleEntity> findAllByIdOrSomeValueOrderByIdDesc(Long id, String val);

    Optional<SampleEntity> findOneById(Long id1);

    Optional<SampleEntity> poorlyNamedMethod();

    List<SampleEntity> findAllByIdAndSomeValue(Long id);

    Set<SampleRepository> findAllById(Long id);
  }
}
