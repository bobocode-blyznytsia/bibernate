package com.bobocode.blyznytsia.bibernate.persister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceException;
import com.bobocode.blyznytsia.bibernate.testdata.entity.NonExistingEntity;
import com.bobocode.blyznytsia.bibernate.testdata.entity.SampleEntity;
import com.bobocode.blyznytsia.bibernate.testdata.mapper.SampleEntityResultSetMapper;
import com.bobocode.blyznytsia.bibernate.util.ResultSetMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseNameGenerator.class)
class EntityPersisterImplTest {

  Connection connection;
  EntityPersister entityPersister;

  ResultSetMapper mockResultSetMapper = new SampleEntityResultSetMapper();

  @BeforeEach
  public void getConnection() throws SQLException {
    connection = DriverManager.getConnection(
        "jdbc:h2:mem:~/test;" +
            "MODE=PostgreSQL;" +
            "INIT=runscript from './src/test/resources/sql/init.sql'");
    entityPersister = new EntityPersisterImpl(connection, mockResultSetMapper);
  }

  @Nested
  class FindAll {
    @Test
    void returnsListOfValuesByKey() throws NoSuchFieldException {
      var keyField = SampleEntity.class.getDeclaredField("someValue");
      var expectedList = List.of(
          new SampleEntity(1L, "val1"),
          new SampleEntity(2L, "val1")
      );
      var actualList = entityPersister.findAll(SampleEntity.class, keyField, "val1");
      assertEquals(expectedList, actualList);
    }

    @Test
    void returnsEmptyListIfNoRecordsFound() throws NoSuchFieldException {
      var keyField = SampleEntity.class.getDeclaredField("someValue");
      var results = entityPersister.findAll(SampleEntity.class, keyField, "non-existent value");
      assertTrue(results.isEmpty());
    }


  }

  @Nested
  class FindOne {
    @Test
    void returnsEntityByKey() throws NoSuchFieldException {
      var keyField = SampleEntity.class.getDeclaredField("id");
      var expectedEntity = new SampleEntity(1L, "val1");
      var actualEntityOptional = entityPersister.findOneBy(SampleEntity.class, keyField, 1L);
      assertTrue(actualEntityOptional.isPresent());
      assertEquals(expectedEntity, actualEntityOptional.get());
    }

    @Test
    void returnsEmptyOptionalWhenNoValueFound() throws NoSuchFieldException {
      var keyField = SampleEntity.class.getDeclaredField("id");
      assertTrue(entityPersister.findOneBy(SampleEntity.class, keyField, -1L).isEmpty());
    }

    @Test
    void throwsPersistenceExceptionIfMultipleRowsFound() throws NoSuchFieldException {
      var keyField = SampleEntity.class.getDeclaredField("someValue");
      assertThrows(PersistenceException.class, () -> entityPersister.findOneBy(SampleEntity.class, keyField, "val1"));
    }
  }

  @Nested
  class FindById {
    @Test
    void returnsEntityById() {
      var expectedEntity = new SampleEntity(1L, "val1");
      var actualEntityOptional = entityPersister.findById(SampleEntity.class, 1L);
      assertTrue(actualEntityOptional.isPresent());
      assertEquals(expectedEntity, actualEntityOptional.get());
    }

    @Test
    void returnsEmptyOptionalWhenNoValueFound() {
      var nonExistingEntityOptional = entityPersister.findById(SampleEntity.class, -1L);
      assertTrue(nonExistingEntityOptional.isEmpty());
    }

    @Test
    void throwsIllegalArgumentExceptionWhenIdIsNull() {
      assertThrows(IllegalArgumentException.class, () -> entityPersister.findById(SampleEntity.class, null));
    }

  }

  @Nested
  class Insert {
    @Test
    void persistsDataInTheDatabase() {
      SampleEntity sampleEntity = new SampleEntity(null, "val 4");
      entityPersister.insert(sampleEntity);
      var entityFromDatabaseOptional = getEntityFromDB(sampleEntity.getId());
      assertTrue(entityFromDatabaseOptional.isPresent());
      assertEquals(sampleEntity, entityFromDatabaseOptional.get());
    }

    @Test
    void setsEntityGeneratedId() {
      SampleEntity sampleEntity = new SampleEntity(null, "val 4");
      entityPersister.insert(sampleEntity);
      assertNotNull(sampleEntity.getId());
    }

    @Test
    void wrapsSQLExceptions() throws NoSuchFieldException {
      var nonExistingEntity = new NonExistingEntity();
      var exception = assertThrows(PersistenceException.class, () -> entityPersister.insert(nonExistingEntity));
      assertTrue(exception.getCause() instanceof SQLException);
    }
  }

  @Nested
  class Update {
    @Test
    void changesAnEntityInTheDatabase() {
      var entityToUpdate = new SampleEntity(1001L, "valueAfterUpdate");
      entityPersister.update(entityToUpdate);
      var entityFromDBOptional = getEntityFromDB(1001L);
      assertTrue(entityFromDBOptional.isPresent());
      assertEquals(entityToUpdate, entityFromDBOptional.get());
    }

    @Test
    void throwsPersistenceExceptionWhenEntityIsTransient() {
      var entityToUpdate = new SampleEntity(null, null);
      assertThrows(PersistenceException.class, () -> entityPersister.update(entityToUpdate));
    }
  }

  @Nested
  class Delete {
    @Test
    void deletesEntity() {
      var entityToDelete = new SampleEntity(1002L, null);
      entityPersister.delete(entityToDelete);
      assertTrue(getEntityFromDB(1002L).isEmpty());
    }

    @Test
    void throwsPersistenceExceptionWhenEntityIsTransient() {
      var entityToDelete = new SampleEntity(null, null);
      assertThrows(PersistenceException.class, () -> entityPersister.delete(entityToDelete));
    }
  }

  private Optional<SampleEntity> getEntityFromDB(Long id) {
    try (var stmt = connection.prepareStatement("SELECT * FROM sample_entity WHERE id = ?")) {
      stmt.setLong(1, id);
      var rs = stmt.executeQuery();
      rs.next();
      return Optional.of(new SampleEntityResultSetMapper().mapToEntity(rs, SampleEntity.class));
    } catch (SQLException e) {
      return Optional.empty();
    }
  }


  @AfterEach
  public void closeConnection() throws SQLException {
    connection.close();
  }

}
