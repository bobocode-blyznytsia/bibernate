package com.bobocode.blyznytsia.bibernate.util;

import static com.bobocode.blyznytsia.bibernate.util.CaseUtil.camelToSnakeCase;

import com.bobocode.blyznytsia.bibernate.annotation.Column;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.Table;
import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.bobocode.blyznytsia.bibernate.exception.MalformedEntityException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;

/**
 * Utility class for performing common operations with entities
 */
@UtilityClass
public class EntityUtil {

  /**
   * Resolves the name of the table for an entity class.
   *
   * @param entityType the entity class to resolve the table name for
   * @return the name of the table for the given entity class
   */
  public static String resolveEntityTableName(Class<?> entityType) {
    return Optional
        .ofNullable(entityType.getAnnotation(Table.class))
        .map(Table::name)
        .orElse(camelToSnakeCase(entityType.getSimpleName()));
  }

  /**
   * Resolves the primary key field for an entity class.
   *
   * @param entityType the entity class to resolve the primary key field for
   * @return the primary key field for the given entity class
   * @throws MalformedEntityException if the entity class does not contain a primary key field or has more than one
   */
  public static Field resolveEntityIdField(Class<?> entityType) {
    var idFields = Arrays.stream(entityType.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Id.class))
        .toList();
    if (idFields.isEmpty()) {
      throw new MalformedEntityException("Entity of type " + entityType.getSimpleName()
          + " must contain a primary key field annotated with @Id");
    }
    if (idFields.size() > 1) {
      throw new MalformedEntityException("Entity of type " + entityType.getSimpleName()
          + " must contain only one @Id field");
    }
    return idFields.get(0);
  }

  /**
   * Resolves the column name for a given field.
   *
   * @param field the field to resolve the column name for
   * @return the name of the column for the given field
   */
  public static String resolveFieldColumnName(Field field) {
    return Optional.ofNullable(field.getAnnotation(Column.class))
        .map(Column::name)
        .orElse(camelToSnakeCase(field.getName()));
  }

  /**
   * Returns a list of non-primary-key fields for an entity object.
   *
   * @param entityType the entity class to get the fields for
   * @return a list of non-primary-key fields for the given entityType
   */
  public static List<Field> getEntityNonIdFields(Class<?> entityType) {
    return Arrays.stream(entityType.getDeclaredFields())
        .filter(field -> !field.isAnnotationPresent(Id.class))
        .toList();
  }

  /**
   * Returns a list of non-primary-key values for an entity object.
   *
   * @param entity the entity object to get the values for
   * @return a list of non-primary-key values for the given entity object
   */
  public static List<Object> getEntityNonIdValues(Object entity) {
    return getEntityNonIdFields(entity.getClass()).stream()
        .map(field -> getFieldValue(field, entity))
        .toList();
  }

  /**
   * Returns the value of the primary key field for an entity object.
   *
   * @param entity the entity object to get the primary key value for
   * @return the value of the primary key field for the given entity object
   */
  public static Object getEntityIdValue(Object entity) {
    return getFieldValue(resolveEntityIdField(entity.getClass()), entity);
  }

  private Object getFieldValue(Field field, Object obj) {
    try {
      field.setAccessible(true);
      return field.get(obj);
    } catch (IllegalAccessException e) {
      throw new BibernateException(e.getMessage(), e);
    }
  }

}
