package com.bobocode.blyznytsia.bibernate.context;

import com.bobocode.blyznytsia.bibernate.model.EntityKey;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entityCache.get(entry.getKey())));
  }

  @Override
  public Object getCachedEntity(EntityKey entityKey) {
    log.debug("Looking up entity of type {} with primary key value = {} from persistence context. ", entityKey.entityType(), entityKey.entityId());
    var entity = entityCache.get(entityKey);
    if (entity == null) {
      log.debug("Entity of type {} with primary key value = {} is not found in persistence context.", entityKey.entityType(), entityKey.entityId());
    } else {
      log.debug("Returning entity of type {} with primary key value = {} from persistence context.", entityKey.entityType(), entityKey.entityId());
    }
    return entity;
  }

  @Override
  public void addEntityToCache(EntityKey entityKey, Object entity) {
    entityCache.put(entityKey, entity);
    entitySnapshots.put(entityKey, new EntitySnapshot(entity));
  }

  private boolean isEntityDirty(EntityKey entityKey) {
    EntitySnapshot entitySnapshot = entitySnapshots.get(entityKey);
    Object entity = entityCache.get(entityKey);
    return entitySnapshot.isDirty(entity);
  }
}
