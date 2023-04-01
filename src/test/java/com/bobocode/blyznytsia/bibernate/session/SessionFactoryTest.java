package com.bobocode.blyznytsia.bibernate.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionFactoryTest {

  @Mock
  private Session session;
  @Mock
  private DataSource dataSource;
  @Mock
  private HikariDataSource hikariDataSource;

  @Test
  void createsSessionFactory() {
    var sessionFactory = new SessionFactoryImpl(dataSource);
    assertTrue(sessionFactory.isOpen());
  }

  @Test
  void requiresNonNullDataSource() {
    NullPointerException exception = assertThrows(
        NullPointerException.class,
        () -> new SessionFactoryImpl(null)
    );
    assertEquals("DataSource must not be null", exception.getMessage());
  }

  @Test
  void opensNewSession() {
    SessionFactoryImpl sessionFactory = new SessionFactoryImpl(dataSource);

    Session session = sessionFactory.openSession();

    assertNotNull(session);
    assertEquals(1, sessionFactory.getSessions().size());
  }

  @Test
  void throwsExceptionIfSessionFactoryIsClosed() throws SQLException {
    when(dataSource.unwrap(Mockito.eq(HikariDataSource.class)))
        .thenReturn(hikariDataSource);
    SessionFactoryImpl sessionFactory = new SessionFactoryImpl(dataSource);
    sessionFactory.close();

    BibernateException exception = assertThrows(
        BibernateException.class,
        sessionFactory::openSession
    );

    assertEquals("SessionFactory is closed", exception.getMessage());
  }

  @Test
  void closesSessionFactory() throws SQLException {
    when(dataSource.unwrap(Mockito.eq(HikariDataSource.class)))
        .thenReturn(hikariDataSource);
    SessionFactoryImpl sessionFactory = new SessionFactoryImpl(dataSource);
    sessionFactory.close();

    assertFalse(sessionFactory.isOpen());
    assertTrue(sessionFactory.getSessions().isEmpty());
  }

  @Test
  void throwsExceptionOnDataSourceClose() throws SQLException {
    when(dataSource.unwrap(Mockito.eq(HikariDataSource.class)))
        .thenReturn(hikariDataSource);
    doThrow(SQLException.class).when(hikariDataSource).close();
    SessionFactoryImpl sessionFactory = new SessionFactoryImpl(dataSource);

    BibernateException exception = assertThrows(
        BibernateException.class,
        sessionFactory::close
    );

    assertEquals("Exception during closing DataSource", exception.getMessage());
  }

  @Test
  void removesSessionFromInternalState() {
    SessionFactoryImpl sessionFactory = new SessionFactoryImpl(dataSource);

    Session session = sessionFactory.openSession();
    assertFalse(sessionFactory.getSessions().isEmpty());

    sessionFactory.removeSession(session);
    assertTrue(sessionFactory.getSessions().isEmpty());
  }

}
