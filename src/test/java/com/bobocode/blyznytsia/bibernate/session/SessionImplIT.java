package com.bobocode.blyznytsia.bibernate.session;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(CamelCaseNameGenerator.class)
class SessionImplIT {

	private Connection connection;
	private SessionImpl session;

	@BeforeEach
	public void getConnection() throws SQLException {
		connection = DriverManager.getConnection(
			"jdbc:h2:mem:~/test;" +
				"MODE=PostgreSQL;" +
				"INIT=runscript from './src/test/resources/sql/init.sql'");
		session = new SessionImpl(connection, sessionToBeClosed -> System.out.println("Session " + sessionToBeClosed + " is closed"));
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
	class Insert {
		@Test
		void insertInAutoCommitMode() {
			var entityToInsert = new SampleEntity();
			entityToInsert.setSomeValue("Text here");
			session.persist(entityToInsert);
			SampleEntity actualEntity = session.findOneBy(SampleEntity.class, "some_value", "Text here");
			assertEquals(entityToInsert.getId(), actualEntity.getId());
		}
	}

	@AfterEach
	public void closeConnection() throws SQLException {
		connection.close();
	}

}
