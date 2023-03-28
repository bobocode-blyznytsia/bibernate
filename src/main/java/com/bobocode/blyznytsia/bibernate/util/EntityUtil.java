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


@UtilityClass
public class EntityUtil {
  public static String resolveEntityTableName(Class<?> entityType) {
    return Optional
        .ofNullable(entityType.getAnnotation(Table.class))
        .map(Table::name)
        .orElse(camelToSnakeCase(entityType.getSimpleName()));
  }

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

  public static String resolveFieldColumnName(Field field) {
    return Optional.ofNullable(field.getAnnotation(Column.class))
        .map(Column::name)
        .orElse(camelToSnakeCase(field.getName()));
  }

  public static List<Field> getEntityNonIdFields(Class<?> entityType) {
    return Arrays.stream(entityType.getDeclaredFields())
        .filter(field -> !field.isAnnotationPresent(Id.class))
        .toList();
  }

  public static List<Object> getEntityNonIdValues(Object entity) {
    return getEntityNonIdFields(entity.getClass()).stream()
        .map(field -> getFieldValue(field, entity))
        .toList();
  }

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
