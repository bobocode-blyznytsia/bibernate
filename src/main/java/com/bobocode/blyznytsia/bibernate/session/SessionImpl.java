package com.bobocode.blyznytsia.bibernate.session;

import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityIdValue;

import com.bobocode.blyznytsia.bibernate.context.PersistenceContext;
import com.bobocode.blyznytsia.bibernate.context.PersistenceContextImpl;
import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.bobocode.blyznytsia.bibernate.exception.EntityNotFoundException;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceException;
import com.bobocode.blyznytsia.bibernate.model.EntityKey;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersisterImpl;
import com.bobocode.blyznytsia.bibernate.query.Query;
import com.bobocode.blyznytsia.bibernate.query.TypedQuery;
import com.bobocode.blyznytsia.bibernate.transaction.Transaction;
import com.bobocode.blyznytsia.bibernate.transaction.TransactionImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionImpl implements Session {

  private final PersistenceContext persistenceContext;

  private final EntityPersister entityPersister;

  private final Connection connection;

  private Transaction transaction;

  private final Consumer<Session> removeSessionConsumer;

  private boolean opened;

  public SessionImpl(Connection connection, Consumer<Session> removeSessionConsumer) {
    this.connection = connection;
    this.removeSessionConsumer = removeSessionConsumer;
    this.persistenceContext = new PersistenceContextImpl();
    this.entityPersister = new EntityPersisterImpl(connection);
    this.opened = true;
  }

  @Override
  public void persist(Object entity) {
    checkTransactionIsAccessible(() -> "Cannot persist. Transaction is not active");
    Object entityIdValue = getEntityIdValue(entity);
    if (entityIdValue == null) {
      insert(entity);
    } else {
      update(entityIdValue, entity);
    }
  }

  @Override
  public void remove(Object entity) {
    checkTransactionIsAccessible(() -> "Cannot persist. Transaction is not active");

    Object entityIdValue = getEntityIdValue(entity);
    var entityKey = new EntityKey(entity.getClass(), entityIdValue);

    if (isConnectionInAutoCommitMode()) {
      try {
        entityPersister.delete(entity);
        persistenceContext.deleteEntityFromCache(entityKey);
      } catch (PersistenceException e) {
        log.error("Cannot remove entity of type {} with primary key={}. {}",
            entity.getClass(), entityIdValue, e.getMessage());
        return;
      }
    }
    persistenceContext.markForDelete(entityKey, entity);
  }

  @Override
  public <T> T find(Class<T> entityClass, Object primaryKey) {
    checkTransactionIsAccessible(() -> "Cannot find entity. Transaction is not active");
    var entityKey = new EntityKey<>(entityClass, primaryKey);
    Object cachedEntity = this.persistenceContext.getCachedEntity(entityKey);
    if (cachedEntity == null) {
      log.debug("Going to get entity of type {} with primary key={} from database",
          entityKey.entityType(), entityKey.entityId());
      cachedEntity = this.entityPersister.findById(entityClass, primaryKey)
          .orElseThrow(() -> new EntityNotFoundException(entityClass, primaryKey));
      this.persistenceContext.addEntityToCache(entityKey, cachedEntity);
    }
    return entityClass.cast(cachedEntity);
  }

  @Override
  public <T> T findOneBy(Class<T> entityClass, String key, Object value) {
    checkTransactionIsAccessible(() -> "Cannot find entity. Transaction is not active");
    var cachedEntity = this.entityPersister.findOneBy(entityClass, key, value)
        .orElseThrow(() -> new EntityNotFoundException(entityClass, key, value));
    var entityKey = new EntityKey<>(entityClass, getEntityIdValue(cachedEntity));
    this.persistenceContext.addEntityToCache(entityKey, cachedEntity);
    return entityClass.cast(cachedEntity);
  }

  @Override
  public void flush() {
    checkTransactionIsAccessible(() -> "Cannot flush. Transaction is not active");

    Map<EntityKey, Object> entityKeyObjectMap = this.persistenceContext.dirtyCheck();
    entityKeyObjectMap.forEach((key, object) -> {
      if (key.entityId() == null) {
        this.entityPersister.insert(object);
      } else if (object == null) {
        // Currently delete operation within transaction is not supported
        entityPersister.delete(object);
      } else {
        this.entityPersister.update(object);
      }
    });

    this.persistenceContext.flush();
  }

  @Override
  public void close() {
    flush();
    try {
      this.connection.close();
    } catch (SQLException e) {
      throw new BibernateException("Cannot close database connection. " + e.getMessage());
    }
    this.opened = false;
    this.removeSessionConsumer.accept(this);
  }

  @Override
  public boolean isOpen() {
    return this.opened;
  }

  @Override
  public Transaction getTransaction() {
    if (this.transaction == null) {
      this.transaction = new TransactionImpl(this.connection, this);
    }
    return this.transaction;
  }

  private boolean isConnectionInAutoCommitMode() {
    try {
      return this.connection.getAutoCommit();
    } catch (SQLException e) {
      throw new PersistenceException("Cannot get connection autocommit mode. " + e.getMessage());
    }
  }

  private void checkTransactionIsAccessible(Supplier<String> errorMessageSupplier) {
    if (!isConnectionInAutoCommitMode() && !transaction.isActive()) {
      throw new PersistenceException(errorMessageSupplier.get());
    }
  }

  private void insert(Object entity) {
    Object entityIdValue = null;
    if (isConnectionInAutoCommitMode()) {
      log.debug("Connection is set to auto commit mode. Going to insert entity to database");
      try {
        entity = this.entityPersister.insert(entity);
        entityIdValue = getEntityIdValue(entity);
      } catch (PersistenceException e) {
        log.error("Cannot insert entity of type {}. {}", entity.getClass(), e.getMessage());
        return;
      }
    }
    var entityKey = new EntityKey(entity.getClass(), entityIdValue);
    this.persistenceContext.markForInsert(entityKey, entity);
  }

  private void update(Object entityIdValue, Object entity) {
    if (isConnectionInAutoCommitMode()) {
      try {
        this.entityPersister.update(entity);
      } catch (PersistenceException e) {
        log.error("Cannot update entity of type {} with primary key={}. {}",
            entity.getClass(), entityIdValue, e.getMessage());
        return;
      }
    }
    var entityKey = new EntityKey(entity.getClass(), entityIdValue);
    this.persistenceContext.markForUpdate(entityKey, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Query<T> createNativeQuery(String sql, Class<T> entityType) {
    return new TypedQuery<>(entityType, sql, entityPersister);
  }
}
