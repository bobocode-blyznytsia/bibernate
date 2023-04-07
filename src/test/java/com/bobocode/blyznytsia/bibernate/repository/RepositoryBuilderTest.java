package com.bobocode.blyznytsia.bibernate.repository;

import static org.mockito.Mockito.when;

import com.bobocode.blyznytsia.bibernate.session.Session;
import com.bobocode.blyznytsia.bibernate.session.SessionFactory;
import com.bobocode.blyznytsia.bibernate.session.SessionImpl;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepositoryBuilderITTest {

Session session;
@Mock
SessionFactory sessionFactory;
  @BeforeEach
  public void getSessionWithInitData() throws SQLException {
     var connection = DriverManager.getConnection(
          "jdbc:h2:mem:~/test;" +
              "MODE=PostgreSQL;" +
              "INIT=runscript from './src/test/resources/sql/init.sql'");
      session = new SessionImpl(connection, sessionToBeClosed -> System.out.println("Session " + sessionToBeClosed + " is closed"));
  }

  @Test
  void name() {
    when(sessionFactory.openSession()).thenReturn(session);
    var repoBuilder = new RepositoryBuilder(sessionFactory);
    var repoProxy = repoBuilder.buildRepository(SampleRepository.class, SampleEntity.class);
    System.out.println(repoProxy.FindByIdOrIdOrIdOrderByIdDesc(1L,3L,2L));
  }

  private interface SampleRepository extends BibernateReadonlyRepository<SampleEntity, Long> {
    List<SampleEntity> FindByIdOrIdOrIdOrderByIdDesc(Long id1, Long id2, Long id3);
  }
}