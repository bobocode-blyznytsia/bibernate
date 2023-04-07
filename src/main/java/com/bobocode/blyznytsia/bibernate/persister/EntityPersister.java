package com.bobocode.blyznytsia.bibernate.persister;

import java.util.List;
import java.util.Optional;

/**
 * This interface defines methods for performing persistence operations with entities.
 */
public interface EntityPersister {
  /**
   * Searches for an entity of the specified type with the given primary key value.
   *
   * @param entityType the type of entity to be found.
   * @param id         the non-null primary key value to search for.
   * @return an {@code Optional} containing the found entity, or {@code Optional.empty()} if no entity was found.
   */
  <T> Optional<T> findById(Class<T> entityType, Object id);

  /**
   * Searches for a single entity of the specified type that has the specified field value.
   *
   * @param entityType the type of entity to search for.
   * @param key        the database column name to search for.
   * @param value      the value to search for in the specified field.
   * @return an {@code Optional} containing the found entity, or {@code Optional.empty()} if no entity was found.
   */
  <T> Optional<T> findOneBy(Class<T> entityType, String key, Object value);

  /**
   * Searches for all entities of the specified type that have the specified field value.
   *
   * @param entityType the type of entity to search for.
   * @param key        the database column name to search for.
   * @param value      the value to search for in the specified field.
   * @return a {@code List} containing all found entities, or an empty list if no entities were found.
   */
  <T> List<T> findAllBy(Class<T> entityType, String key, Object value);

  /**
   * Executes a query and returns a list of entities that match the given query and parameters.
   *
   * @param entityType the {@code Class} object representing the entity type
   * @param query      the SQL query to execute
   * @param params     the parameters for the query
   * @return a {@code List} of entities that match the given query and parameters
   */
  <T> List<T> findAllByQuery(Class<T> entityType, String query, Object... params);

  /**
   * Executes a query and returns an {@code Optional} containing a single entity that matches
   * the given query and parameters.
   * If the query returns more than one entity, a {@code PersistenceException} is thrown.
   *
   * @param entityType the {@code Class} object representing the entity type
   * @param query      the SQL query to execute
   * @param params     the parameters for the query
   * @return an {@code Optional} containing a single entity that matches the given query and parameters,
   *     or an empty {@code Optional} if no result is found
   */
  <T> Optional<T> findOneByQuery(Class<T> entityType, String query, Object... params);


  /**
   * Inserts a new entity into the database.
   *
   * @param entity the entity to insert.
   * @return the newly-inserted entity.
   */
  <T> T insert(T entity);

  /**
   * Updates an existing entity in the database.
   *
   * @param entity the entity to update.
   */
  <T> void update(T entity);

  /**
   * Deletes an entity from the database.
   *
   * @param entity the entity to delete.
   */
  <T> void delete(T entity);
}
