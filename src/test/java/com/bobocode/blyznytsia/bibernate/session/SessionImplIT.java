package com.bobocode.blyznytsia.bibernate.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersisterImpl;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(CamelCaseNameGenerator.class)
class SessionImplIT {

	private Connection connection;
	private SessionImpl session;
	private  EntityPersister entityPersister;

	@BeforeEach
	public void getConnection() throws SQLException {
		connection = DriverManager.getConnection(
			"jdbc:h2:mem:~/test;" +
				"MODE=PostgreSQL;" +
				"INIT=runscript from './src/test/resources/sql/init.sql'");
		session = new SessionImpl(connection, sessionToBeClosed -> System.out.println("Session " + sessionToBeClosed + " is closed"));
		entityPersister = new EntityPersisterImpl(connection);
	}

	@Nested
	class Find {
		@Test
		void findById() {
			SampleEntity actualEntity = session.find(SampleEntity.class, 1L);
			SampleEntity expectedEntity = new SampleEntity(1L, "val1");
			assertEquals(expectedEntity, actualEntity);
		}

		@Test
		void findBy() {
			SampleEntity actualEntity = session.findOneBy(SampleEntity.class, "some_value", "val2");
			SampleEntity expectedEntity = new SampleEntity(3L, "val2");
			assertEquals(expectedEntity, actualEntity);
		}
	}

	@Nested
	class Delete {
		@Test
		void deleteInAutoCommitMode() {
			SampleEntity entity = new SampleEntity(1L, "val1"); // detached

			session.remove(entity);

			Optional<SampleEntity> actualEntity = entityPersister.findById(SampleEntity.class, 1L);
			assertTrue(actualEntity.isEmpty());
		}

		@Test
		void deleteInTransactionWithCommit() {
			SampleEntity entity = new SampleEntity(1L, "val1");  // just forbid case when entity is in detached state
 //
			session.getTransaction().begin();
			session.remove(entity);
			session.getTransaction().commit();

			Optional<SampleEntity> actualEntity = entityPersister.findById(SampleEntity.class, 1L);
			assertTrue(actualEntity.isEmpty());
		}
	}

	@Nested
	class Insert {
		@Test
		void insertInAutoCommitMode() {
			var entityToInsert = new SampleEntity();
			entityToInsert.setSomeValue("Text here");

			session.persist(entityToInsert);

			Optional<SampleEntity> actualEntity = entityPersister.findOneBy(SampleEntity.class, "some_value", "Text here");
			assertFalse(actualEntity.isEmpty());
			assertEquals(entityToInsert.getSomeValue(), actualEntity.get().getSomeValue());
		}

		@Test
		void insertInTransactionWithCommit() {
			var entityToInsert = new SampleEntity();
			entityToInsert.setId(10L); // just forbid this case
			entityToInsert.setSomeValue("Text here");

			session.getTransaction().begin();
			session.persist(entityToInsert);
			session.getTransaction().commit();

			Optional<SampleEntity> actualEntity = entityPersister.findById(SampleEntity.class, 10L);
			assertFalse(actualEntity.isEmpty());
			assertEquals(entityToInsert.getSomeValue(), actualEntity.get().getSomeValue());
		}

		@Test
		void insertInTransactionWithRollback() {
			long entityId = 10L;
			var entityToInsert = new SampleEntity();
			entityToInsert.setId(entityId);
			entityToInsert.setSomeValue("Text here");

			session.getTransaction().begin();
			session.persist(entityToInsert);
			session.getTransaction().rollback();

			Optional<SampleEntity> actualEntity = entityPersister.findById(SampleEntity.class, 10L);
			assertTrue(actualEntity.isEmpty());
		}
	}

	@Nested
	class Update {
		@Test
		void updateInAutoCommitMode() {
			var entityToUpdate = new SampleEntity();
			entityToUpdate.setId(1L);
			entityToUpdate.setSomeValue("Updated text here");

			session.persist(entityToUpdate);

			Optional<SampleEntity> actualEntity = entityPersister.findById(SampleEntity.class, 1L);
			assertFalse(actualEntity.isEmpty());
			assertEquals(entityToUpdate.getSomeValue(), actualEntity.get().getSomeValue());
		}

		@Test
		void updateInTransactionWithCommit() {
			var entityToUpdate = new SampleEntity();
			entityToUpdate.setId(1L);
			entityToUpdate.setSomeValue("Updated text in transaction here");

			session.getTransaction().begin();
			session.persist(entityToUpdate);
			session.getTransaction().commit();

			Optional<SampleEntity> actualEntity = entityPersister.findById(SampleEntity.class, 1L);
			assertFalse(actualEntity.isEmpty());
			assertEquals(entityToUpdate.getSomeValue(), actualEntity.get().getSomeValue());
		}

		@Test
		void updateInTransactionWithRollback() {
			var entityToUpdate = new SampleEntity();
			entityToUpdate.setId(1L);
			entityToUpdate.setSomeValue("Updated text in transaction here");

			session.getTransaction().begin();
			session.persist(entityToUpdate);
			session.getTransaction().rollback();

			Optional<SampleEntity> actualEntity = entityPersister.findById(SampleEntity.class, 1L);
			assertFalse(actualEntity.isEmpty());
			assertEquals("val1", actualEntity.get().getSomeValue());
		}
	}

	@AfterEach
	public void closeConnection() throws SQLException {
		connection.close();
	}

}
