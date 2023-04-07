package com.bobocode.blyznytsia.bibernate.context;

import com.bobocode.blyznytsia.bibernate.model.EntityKey;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import java.util.Map;

/**
 * Defines the contract for a persistence context that manages a cache of entities.
 * It provides methods for performing dirty checks, retrieving cached entities, and adding entities
 * to the cache.
 */
public interface PersistenceContext {

  /**
   * Performs a dirty check by comparing the current state of entities in the cache
   * with their original state.
   *
   * @return A map containing EntityKeys and the corresponding dirty entities.
   */
  Map<EntityKey, Object> dirtyCheck();


  /**
   * Performs synchronization of current state of entities in the cache
   * with their original state.
   */
  void flush();

  /**
   * Retrieves the entity associated with the given EntityKey from the cache.
   *
   * @param entityKey The EntityKey used to locate the desired entity.
   * @return The entity associated with the provided EntityKey, or null if not found.
   */
  Object getCachedEntity(EntityKey entityKey);

  /**
   * Adds an entity to the cache. Is used in conjunction with {@link EntityPersister} findOneBy, findById methods
   *
   * @param entityKey The EntityKey used to identify the entity in the cache.
   * @param entity The entity to be added to the cache.
   */
  void addEntityToCache(EntityKey entityKey, Object entity);

  /**
   * Removes an entity from the cache.
   *
   * @param entityKey The EntityKey used to identify the entity in the cache.
   */
  void deleteEntityFromCache(EntityKey entityKey);

  /**
   * Marks entity for delete.
   *
   * @param entityKey The EntityKey used to identify the entity in the cache.
   * @param entity The entity to be marked for deletion.
   */
  void markForDelete(EntityKey entityKey, Object entity);

  /**
   * Marks entity for insert.
   *
   * @param entityKey The EntityKey used to identify the entity in the cache.
   * @param entity The entity to be marked for insert.
   */
  void markForInsert(EntityKey entityKey, Object entity);

  /**
   * Marks entity for update.
   *
   * @param entityKey The EntityKey used to identify the entity in the cache.
   * @param entity The entity to be marked for update.
   */
  void markForUpdate(EntityKey entityKey,  Object entity);

}

