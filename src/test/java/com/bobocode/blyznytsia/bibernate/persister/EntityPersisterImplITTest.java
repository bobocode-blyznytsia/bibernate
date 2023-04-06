package com.bobocode.blyznytsia.bibernate.persister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceException;
import com.bobocode.blyznytsia.bibernate.exception.TransientEntityException;
import com.bobocode.blyznytsia.bibernate.mapper.ResultSetMapperImpl;
import com.bobocode.blyznytsia.bibernate.testdata.NonExistingEntity;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
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

@DisplayNameGeneration(CamelCaseNameGenerator.class)
class EntityPersisterImplITTest {

  private Connection connection;
  private EntityPersister entityPersister;

  @BeforeEach
  public void getConnection() throws SQLException {
    connection = DriverManager.getConnection(
        "jdbc:h2:mem:~/test;" +
            "MODE=PostgreSQL;" +
            "INIT=runscript from './src/test/resources/sql/init.sql'");
    entityPersister = new EntityPersisterImpl(connection);
  }

  @Nested
  class FindAll {
    @Test
    void returnsListOfValuesByKey() {
      var keyField = "some_value";
      var expectedList = List.of(
          new SampleEntity(1L, "val1"),
          new SampleEntity(2L, "val1")
      );
      var actualList = entityPersister.findAllBy(SampleEntity.class, keyField, "val1");
      assertEquals(expectedList, actualList);
    }

    @Test
    void returnsEmptyListIfNoRecordsFound() {
      var keyField = "some_value";
      var results = entityPersister.findAllBy(SampleEntity.class, keyField, "non-existent value");
      assertTrue(results.isEmpty());
    }

  }

  @Nested
  class FindOne {
    @Test
    void returnsEntityByKey() throws NoSuchFieldException {
      var keyField = SampleEntity.class.getDeclaredField("id");
      var expectedEntity = new SampleEntity(1L, "val1");
      var actualEntityOptional = entityPersister.findOneBy(SampleEntity.class, keyField.getName(), 1L);
      assertTrue(actualEntityOptional.isPresent());
      assertEquals(expectedEntity, actualEntityOptional.get());
    }

    @Test
    void returnsEmptyOptionalWhenNoValueFound() throws NoSuchFieldException {
      var keyField = SampleEntity.class.getDeclaredField("id");
      assertTrue(entityPersister.findOneBy(SampleEntity.class, keyField.getName(), -1L).isEmpty());
    }

    @Test
    void throwsPersistenceExceptionIfMultipleRowsFound() throws NoSuchFieldException {
      var keyField = SampleEntity.class.getDeclaredField("someValue");
      assertThrows(PersistenceException.class, () -> entityPersister.findOneBy(SampleEntity.class,
          keyField.getName(), "val1"));
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
    void throwsNpeExceptionWhenIdIsNull() {
      assertThrows(NullPointerException.class, () -> entityPersister.findById(SampleEntity.class, null));
    }

  }

  @Nested
  class FinOneByQuery {
    @Test
    void returnsEntityIfPresent() {
      var searchParam = "val2";
      var expectedEntity = new SampleEntity(3L, searchParam);
      var sql = "SELECT * FROM sample_entity where some_value = ?";
      var actualEntityOptional = entityPersister.findOneByQuery(SampleEntity.class, sql, searchParam);
      assertTrue(actualEntityOptional.isPresent());
      assertEquals(expectedEntity, actualEntityOptional.get());
    }
    @Test
    void returnsEmptyOptionalIfNoValuePresent() {
      var sql = "SELECT * FROM sample_entity WHERE FALSE";
      var actualEntityOptional = entityPersister.findOneByQuery(SampleEntity.class, sql);
      assertTrue(actualEntityOptional.isEmpty());
    }

    @Test
    void throwsPersistenceExceptionIfMultipleValuesFound() {
      var searchParam = "val1"; // there are 2 records with such value in the DB
      var sql = "SELECT * FROM sample_entity where some_value = ?";
      assertThrows(PersistenceException.class,() -> entityPersister.findOneByQuery(SampleEntity.class, sql, searchParam));
    }
  }

  @Nested
  class FindAllByQuery{
    @Test
    void returnsListOfValuesByKey() {
      var expectedList = List.of(
          new SampleEntity(1L, "val1"),
          new SampleEntity(2L, "val1")
      );
      var sql = "SELECT * FROM sample_entity WHERE some_value = ?";
      var actualList = entityPersister.findAllByQuery(SampleEntity.class, sql , "val1");
      assertEquals(expectedList, actualList);
    }

    @Test
    void returnsEmptyListIfNoRecordsFound() {
      var sql = "SELECT * FROM sample_entity WHERE FALSE";
      var results = entityPersister.findAllByQuery(SampleEntity.class, sql);
      assertTrue(results.isEmpty());
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
    void throwsTransientEntityExceptionWhenEntityIsTransient() {
      var entityToUpdate = new SampleEntity(null, null);
      assertThrows(TransientEntityException.class, () -> entityPersister.update(entityToUpdate));
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
    void throwsTransientEntityExceptionWhenEntityIsTransient() {
      var entityToDelete = new SampleEntity(null, null);
      assertThrows(TransientEntityException.class, () -> entityPersister.delete(entityToDelete));
    }
  }

  private Optional<SampleEntity> getEntityFromDB(Long id) {
    try (var stmt = connection.prepareStatement("SELECT * FROM sample_entity WHERE id = ?")) {
      stmt.setLong(1, id);
      var rs = stmt.executeQuery();
      if (rs.next()) {
        return Optional.of(
            new ResultSetMapperImpl(entityPersister).mapToEntity(rs, SampleEntity.class));
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      return Optional.empty();
    }
  }

  @AfterEach
  public void closeConnection() throws SQLException {
    connection.close();
  }

}
