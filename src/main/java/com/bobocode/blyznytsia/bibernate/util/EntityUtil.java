package com.bobocode.blyznytsia.bibernate.util;

import static com.bobocode.blyznytsia.bibernate.util.CaseUtil.camelToSnakeCase;

import com.bobocode.blyznytsia.bibernate.annotation.Column;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.Table;
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
    return Arrays.stream(entityType.getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(Id.class))
        .findFirst()
        .orElseThrow(() -> new MalformedEntityException("Entity of type " + entityType.getSimpleName()
            + " must contain a primary key field annotated with @Id"));
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

}
