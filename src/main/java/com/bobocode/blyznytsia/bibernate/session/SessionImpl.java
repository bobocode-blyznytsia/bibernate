package com.bobocode.blyznytsia.bibernate.session;

import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityIdValue;

import com.bobocode.blyznytsia.bibernate.context.PersistenceContext;
import com.bobocode.blyznytsia.bibernate.context.PersistenceContextImpl;
import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceException;
import com.bobocode.blyznytsia.bibernate.model.EntityKey;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersisterImpl;
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

	public SessionImpl(Connection connection,
		Consumer<Session> removeSessionConsumer) {
		this.connection = connection;
		this.removeSessionConsumer = removeSessionConsumer;
		this.persistenceContext = new PersistenceContextImpl();
		this.entityPersister = new EntityPersisterImpl(connection);
		this.opened = true;
	}

	@Override
	public void persist(Object entity) {
		checkTransactionIsAccessible(() -> "Cannot persist. Transaction is not active.");
		Object entityIdValue = getEntityIdValue(entity);
		if (entityIdValue == null) {
			insert(entity);
		} else {
			update(entityIdValue, entity);
		}
	}

	@Override
	public void remove(Object entity) {
		checkTransactionIsAccessible(() -> "Cannot persist. Transaction is not active.");

		Object entityIdValue = getEntityIdValue(entity);

		if (isConnectionInAutoCommitMode()) {
			try {
				entityPersister.delete(entity);
				// this.persistenceContext.deleteEntityCache //FIXME
			} catch (PersistenceException e) {
				log.error("Cannot remove entity of type {} with Id {}. Reason: {}", entity.getClass().getSimpleName(), entityIdValue, e.getMessage());
			}
		} else {
			var entityKey = new EntityKey<>(entity.getClass(), entityIdValue);
			Object cachedEntity = this.persistenceContext.getCachedEntity(entityKey); // TODO return optional, will it help to improve code ?
			if (cachedEntity != null) {
				this.persistenceContext.addEntityToCache(entityKey, null);
			}
		}
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		checkTransactionIsAccessible(() -> "Cannot find entity. Transaction is not active.");
		var entityKey = new EntityKey<>(entityClass, primaryKey);
		Object cachedEntity = this.persistenceContext.getCachedEntity(entityKey);
		if (cachedEntity == null) {
			cachedEntity = this.entityPersister.findById(entityClass, primaryKey)
				.orElseThrow(() -> new BibernateException("Entity of type %s with Id %s is not found".formatted(entityClass.getSimpleName(), primaryKey)));
			this.persistenceContext.addEntityToCache(entityKey, cachedEntity);
		}
		return entityClass.cast(cachedEntity);
	}

	@Override
	public void flush() {
		checkTransactionIsAccessible(() -> "Cannot flush. Transaction is not active.");

		Map<EntityKey, Object> entityKeyObjectMap = this.persistenceContext.dirtyCheck();
		entityKeyObjectMap.forEach((key, object) -> {
			if (key.entityId() == null) {
				Object entity = entityPersister.insert(object);
				this.persistenceContext.addEntityToCache(new EntityKey(key.entityType(), getEntityIdValue(entity)), entity);
			} else if (object == null) {
				entityPersister.delete(object); // FIXME change entityPersister's signature ?
				// this.persistenceContext.deleteEntityCache // FIXME ask Sergii
			} else {
				entityPersister.update(object);
			}
		});
	}

	@Override
	public void close() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			throw new BibernateException("Cannot close database connection. Reason: " + e.getMessage());
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
		return this.transaction == null
			? new TransactionImpl(null)
			: this.transaction;
	}

	private boolean isConnectionInAutoCommitMode() {
		try {
			return this.connection.getAutoCommit();
		} catch (SQLException e) {
			throw new BibernateException("Cannot get connection autocommit mode. Reason: " + e.getMessage());
		}
	}

	private void checkTransactionIsAccessible(Supplier<String> errorMessageSupplier) {
		if (!isConnectionInAutoCommitMode() && !transaction.isActive()) {
			throw new BibernateException(errorMessageSupplier.get());
		}
	}

	private void insert(Object entity) {
		if (isConnectionInAutoCommitMode()) {
			try {
				Object insertedEntity = this.entityPersister.insert(entity);
				this.persistenceContext.addEntityToCache(new EntityKey(entity.getClass(), getEntityIdValue(insertedEntity)), insertedEntity);
			} catch (PersistenceException e) {
				log.error("Cannot insert entity of type {}. Reason: {}", entity.getClass().getSimpleName(), e.getMessage());
				return;
			}
		}
		this.persistenceContext.addEntityToCache(new EntityKey(entity.getClass(), null), entity);
	}

	private void update(Object entityIdValue,  Object entity) {
		if (isConnectionInAutoCommitMode()) {
			try {
				this.entityPersister.update(entity);
			} catch (PersistenceException e) {
				log.error("Cannot update entity of type {} with Id {}. Reason: {}", entity.getClass().getSimpleName(), entityIdValue, e.getMessage());
				return;
			}
		}
		this.persistenceContext.addEntityToCache(new EntityKey(entity.getClass(), entityIdValue), entity);
	}

}
