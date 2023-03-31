package com.bobocode.blyznytsia.bibernate.context;

import com.bobocode.blyznytsia.bibernate.model.EntityKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores entity snapshots and manages a cache of entities using their EntityKeys.
 * This class provides methods to perform dirty checks, retrieve cached entities,
 * and add entities to the cache.
 */
@SuppressWarnings("rawtypes")
public class PersistenceContextImpl implements PersistenceContext {

  private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();
  private final Map<EntityKey, Object> entityCache = new HashMap<>();

  @Override
  public Map<EntityKey, Object> dirtyCheck() {
    Map<EntityKey, Object> dirtyEntities = new HashMap<>();
    for (Map.Entry<EntityKey, EntitySnapshot> entry : entitySnapshots.entrySet()) {
      EntityKey key = entry.getKey();
      EntitySnapshot snapshot = entry.getValue();
      Object entity = entityCache.get(key);

      if (snapshot.isDirty(entity)) {
        dirtyEntities.put(key, entity);
      }
    }
    return dirtyEntities;
  }

  @Override
  public Object getCachedEntity(EntityKey entityKey) {
    return entityCache.get(entityKey);
  }

  @Override
  public void addEntityToCache(EntityKey entityKey, Object entity) {
    entityCache.put(entityKey, entity);
    entitySnapshots.put(entityKey, new EntitySnapshot(entity));
  }
}
