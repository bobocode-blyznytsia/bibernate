package com.bobocode.blyznytsia.bibernate.mapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that provides functionality working with mapped entities context: putting in context, getting
 * from context, checking presence in the context.
 */
public class ResultSetMappedEntitiesContext {

  private final Map<Class<?>, Map<Object, Object>> mappedEntities = new HashMap<>();

  /**
   * Puts entity in mapped entities context.
   *
   * @param entityType the entity class
   * @param idValue the entity id value
   * @param entityValue entity instance to put
   */
   public void putEntityInContext(Class<?> entityType, Object idValue, Object entityValue) {
    Map<Object, Object> keysValues = mappedEntities.get(entityType);
    if (keysValues == null) {
      Map<Object, Object> newKeysValuesForType = new HashMap<>();
      newKeysValuesForType.put(idValue, entityValue);
      mappedEntities.put(entityType, newKeysValuesForType);
    } else {
      keysValues.put(idValue, entityValue);
    }
  }

  /**
   * Gets entity from mapped entities context.
   *
   * @param entityType the entity class
   * @param idValue the entity id value
   * @return entity from context
   */
  public Object getEntityFromContext(Class<?> entityType, Object idValue) {
    Map<Object, Object> keysValues = mappedEntities.get(entityType);
    return keysValues.get(idValue);
  }

  /**
   * Checks whether entity is present in mapped entity context.
   *
   * @param entityType the entity class
   * @param idValue the entity id value
   * @return true if entity is present in context
   */
   public boolean isEntityPresentInContext(Class<?> entityType, Object idValue) {
    Map<Object, Object> keysValues = mappedEntities.get(entityType);
    return keysValues != null && keysValues.containsKey(idValue);
  }

}
