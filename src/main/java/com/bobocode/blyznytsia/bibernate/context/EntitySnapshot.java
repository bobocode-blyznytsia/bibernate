package com.bobocode.blyznytsia.bibernate.context;

import com.bobocode.blyznytsia.bibernate.exception.PersistenceException;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Stores a deep copy of the provided entity object and provides a method to check if the current
 * state of the entity is different (dirty) from its original state.
 */
public class EntitySnapshot {
  private final Object originalEntityState;

  public EntitySnapshot(Object entity) {
    this.originalEntityState = deepCopy(entity);
  }

  /**
   * Determines if the given entity is dirty (has changed) by comparing its current state
   * with the original state stored in this EntitySnapshot instance.
   *
   * @param entity The entity object to compare with the original state.
   * @return true if the entity's state is different from its original state, false otherwise.
   * @throws IllegalArgumentException if the given entity is null or not of the same class as the original entity.
   */
  public boolean isDirty(Object entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Entity cannot be null");
    }

    if (!entity.getClass().equals(originalEntityState.getClass())) {
      throw new IllegalArgumentException(
          "Entity and originalEntityState must be of the same class");
    }

    return isDirtyRecursive(entity, originalEntityState);
  }

  private boolean isDirtyRecursive(Object currentValue, Object originalValue) {
    for (Field field : currentValue.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        Object currentFieldValue = field.get(currentValue);
        Object originalFieldValue = field.get(originalValue);

        if (!Objects.equals(currentFieldValue, originalFieldValue) &&
            (currentFieldValue == null || shouldUseEquals(field) ||
                isDirtyRecursive(currentFieldValue, originalFieldValue))) {
          return true;
        }
      } catch (IllegalAccessException e) {
        throw new PersistenceException("Failed to access entity field value", e);
      }
    }
    return false;
  }

  private boolean shouldUseEquals(Field field) {
    Class<?> fieldType = field.getType();
    String fieldTypeName = fieldType.getName();
    return fieldType.isPrimitive() || fieldTypeName.startsWith("java.lang") ||
        fieldTypeName.startsWith("java.time");
  }

  private Object deepCopy(Object source) {
    Object target;
    try {
      var sourceClass = source.getClass();
      var constructor = sourceClass.getConstructor();
      target = constructor.newInstance();
    } catch (Exception e) {
      throw new PersistenceException("Failed to create a new instance of the entity", e);
    }
    for (Field field : source.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        Object fieldValue = field.get(source);
        field.set(target, fieldValue);
      } catch (IllegalAccessException e) {
        throw new PersistenceException("Failed to copy entity field value", e);
      }
    }
    return target;
  }
}
