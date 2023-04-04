package com.bobocode.blyznytsia.bibernate.mapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.bobocode.blyznytsia.bibernate.annotation.OneToMany;
import com.bobocode.blyznytsia.bibernate.annotation.OneToOne;
import com.bobocode.blyznytsia.bibernate.exception.MalformedEntityException;
import com.bobocode.blyznytsia.bibernate.exception.NotSupportedException;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import com.bobocode.blyznytsia.bibernate.testdata.Address;
import com.bobocode.blyznytsia.bibernate.testdata.Note;
import com.bobocode.blyznytsia.bibernate.testdata.PersonWithOneToMany;
import com.bobocode.blyznytsia.bibernate.testdata.PersonWithOneToOne;
import com.bobocode.blyznytsia.bibernate.testdata.PlainPerson;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RelationResolverTest {

  private RelationResolver relationResolver;

  @Mock
  private EntityPersister entityPersister;
  @Mock
  private ResultSet resultSet;

  @BeforeEach
  void setUp() {
    relationResolver = new RelationResolver(entityPersister);
  }

  @Test
  void resolveRelationFieldOneToOneOnChildSide() throws NoSuchFieldException, SQLException {
    Field oneToOneField = Address.class.getDeclaredField("person");
    when(resultSet.getObject(anyString())).thenReturn(1L);
    when(entityPersister.findById(PersonWithOneToOne.class, 1L))
        .thenReturn(Optional.of(new PersonWithOneToOne()));

    Object fieldValue = relationResolver.resolveRelationField(oneToOneField, resultSet, new Address());

    assertTrue(fieldValue instanceof PersonWithOneToOne);
  }

  @Test
  void resolveRelationFieldOneToOneOnParentSide() throws NoSuchFieldException, SQLException {
    Field oneToOneField = PersonWithOneToOne.class.getDeclaredField("address");
    when(resultSet.getObject(anyString())).thenReturn(1L);
    when(entityPersister.findOneBy(Address.class, "person_id", 1L))
        .thenReturn(Optional.of(new Address()));

    Object fieldValue = relationResolver.resolveRelationField(oneToOneField, resultSet, new PersonWithOneToOne());

    assertTrue(fieldValue instanceof Address);
  }

  @Test
  void resolveRelationFieldOneToMany() throws NoSuchFieldException, SQLException {
    Field oneToManyField = PersonWithOneToMany.class.getDeclaredField("notes");
    when(resultSet.getObject(anyString())).thenReturn(1L);
    when(entityPersister.findAllBy(Note.class, "person_id", 1L))
        .thenReturn(List.of(new Note()));

    Object fieldValue = relationResolver.resolveRelationField(oneToManyField, resultSet, new PersonWithOneToMany());

    assertTrue(fieldValue instanceof List);
  }

  @Test
  void resolveRelationFieldManyToOne() throws SQLException, NoSuchFieldException {
    Field manyToOneField = Note.class.getDeclaredField("person");
    when(resultSet.getObject(anyString())).thenReturn(1L);
    when(entityPersister.findById(PersonWithOneToMany.class, 1L))
        .thenReturn(Optional.of(new PersonWithOneToMany()));

    Object fieldValue = relationResolver.resolveRelationField(manyToOneField, resultSet, new Note());

    assertTrue(fieldValue instanceof PersonWithOneToMany);
  }

  @Test
  void resolveRelationFieldOneToOneWhenBothJoinColumnNameAndMappedByAreNotSpecified() throws Exception {
    Field oneToOneField = TestEntity.class.getDeclaredField("malformedPerson");

    assertThrows(MalformedEntityException.class,
        () -> relationResolver.resolveRelationField(oneToOneField, resultSet, new TestEntity()));
  }

  @Test
  void resolveRelationFieldOneToManyWhenCollectionIsNotSupported() throws Exception {
    Field oneToOneField = TestEntity.class.getDeclaredField("invalidSet");

    assertThrows(NotSupportedException.class,
        () -> relationResolver.resolveRelationField(oneToOneField, resultSet, new TestEntity()));
  }

  private static class TestEntity {
    @OneToOne(joinColumnName = "", mappedBy = "")
    private PlainPerson malformedPerson;

    @OneToMany(mappedBy = "plainPerson")
    private Set<PlainPerson> invalidSet;

  }

}
