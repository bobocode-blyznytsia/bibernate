package com.bobocode.blyznytsia.bibernate.session;

import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.bobocode.blyznytsia.bibernate.transaction.Transaction;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionFactoryImpl implements SessionFactory {

  private final DataSource dataSource;
  private final Set<Session> sessions;
  private boolean opened;

  public SessionFactoryImpl(DataSource dataSource) {
    log.debug("Initializing SessionFactory");
    Objects.requireNonNull(dataSource, "DataSource must not be null");
    this.dataSource = dataSource;
    this.sessions = ConcurrentHashMap.newKeySet();
    this.opened = true;
    log.debug("SessionFactory initialized");
  }

  // TODO VS: Change to real implementation
  @Override
  public Session openSession() {
    log.debug("Opening the new Session");
    if (!isOpen()) {
      throw new BibernateException("SessionFactory is closed");
    }
    Session newSession = new SessionImpl(this, dataSource, this::removeSession);
    sessions.add(newSession);
    return newSession;
  }

  @Override
  public boolean isOpen() {
    return opened;
  }

  @Override
  public void close() {
    log.debug("Closing SessionFactory");
    log.debug("Closing Sessions");
    sessions.forEach(Session::close);
    sessions.clear();
    closeDataSource();
    opened = false;
    log.debug("SessionFactory is closed");
  }

  public void removeSession(Session session) {
    log.debug("Removing Session from internal collection. {}", session);
    sessions.remove(session);
  }

  Set<Session> getSessions() {
    return new HashSet<>(sessions);
  }

  private void closeDataSource() {
    try {
      var unwrappedDataSource = dataSource.unwrap(HikariDataSource.class);
      unwrappedDataSource.close();
    } catch (SQLException ex) {
      throw new BibernateException("Exception during closing DataSource", ex);
    }
  }

  // TODO VS: remove
  public class SessionImpl implements Session {

    private final Consumer<Session> removeSessionConsumer;

    public SessionImpl(SessionFactoryImpl sessionFactory, DataSource dataSource,
                       Consumer<Session> removeSessionConsumer) {
      this.removeSessionConsumer = removeSessionConsumer;
    }

    @Override
    public void persist(Object entity) {

    }

    @Override
    public void remove(Object entity) {

    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
      return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {
      removeSessionConsumer.accept(this);
    }

    @Override
    public boolean isOpen() {
      return false;
    }

    @Override
    public Transaction getTransaction() {
      return null;
    }
  }
}
