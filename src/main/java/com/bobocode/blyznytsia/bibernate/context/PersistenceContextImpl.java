package com.bobocode.blyznytsia.bibernate.context;

import com.bobocode.blyznytsia.bibernate.model.EntityKey;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Stores entity snapshots and manages a cache of entities using their EntityKeys.
 * This class provides methods to perform dirty checks, retrieve cached entities,
 * and add entities to the cache.
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class PersistenceContextImpl implements PersistenceContext {

  private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();
  private final Map<EntityKey, Object> entityCache = new HashMap<>();

  @Override
  public Map<EntityKey, Object> dirtyCheck() {
    return entitySnapshots.entrySet().stream()
        .filter(entry -> isEntityDirty(entry.getKey()))
        .collect(HashMap::new, (m, v) -> m.put(v.getKey(), entityCache.get(v.getKey())), HashMap::putAll);
  }

  @Override
  public void flush() {
    entityCache.forEach((entityKey, entity) ->
        this.entitySnapshots.put(entityKey, new EntitySnapshot(entity))
    );
  }

  @Override
  public Object getCachedEntity(EntityKey entityKey) {
    Class entityType = entityKey.entityType();
    Object entityId = entityKey.entityId();
    log.debug("Looking up entity of type {} with primary key={} from persistence context", entityType, entityId);
    var entity = entityCache.get(entityKey);
    if (entity == null) {
      log.debug("Entity of type {} with primary key={} is not found in persistence context", entityType, entityId);
    } else {
      log.debug("Returning entity of type {} with primary key={} from persistence context", entityType, entityId);
    }
    return entity;
  }

  @Override
  public void addEntityToCache(EntityKey entityKey, Object entity) {
    log.debug("Adding entity {} with entity key={} to persistence context", entity.getClass(), entityKey);
    entityCache.put(entityKey, entity);
    entitySnapshots.put(entityKey, new EntitySnapshot(entity));
  }

  @Override
  public void deleteEntityFromCache(EntityKey entityKey) {
    log.debug("Removing entity {} with entity key={} from persistence context", entityKey.getClass(), entityKey);
    entityCache.remove(entityKey);
    entitySnapshots.remove(entityKey);
  }

  @Override
  public void markForDelete(EntityKey entityKey, Object entity) {
    log.debug("Marking entity {} with entity key={} for delete", entity.getClass(), entityKey);
    entityCache.put(entityKey, null);
    entitySnapshots.put(entityKey, new EntitySnapshot(entity));
  }

  @Override
  public void markForInsert(EntityKey entityKey, Object entity) {
    log.debug("Marking entity {} with entity key={} for insert", entity.getClass(), entityKey);
    entityCache.put(entityKey, entity);
    entitySnapshots.put(entityKey, null);
  }

  @Override
  public void markForUpdate(EntityKey entityKey,  Object entity) {
    log.debug("Marking entity {} with entity key={} for update", entity.getClass(), entityKey);
    if (!entityCache.containsKey(entityKey)) {
      markForInsert(entityKey, entity);
    }
  }

  private boolean isEntityDirty(EntityKey entityKey) {
    EntitySnapshot entitySnapshot = entitySnapshots.get(entityKey);
    if (entitySnapshot == null) {
      return true;
    }
    Object entity = entityCache.get(entityKey);
    return entitySnapshot.isDirty(entity);
  }
}
