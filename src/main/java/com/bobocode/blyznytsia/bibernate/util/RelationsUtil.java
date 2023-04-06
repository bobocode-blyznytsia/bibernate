package com.bobocode.blyznytsia.bibernate.util;

import com.bobocode.blyznytsia.bibernate.annotation.ManyToOne;
import com.bobocode.blyznytsia.bibernate.annotation.OneToMany;
import com.bobocode.blyznytsia.bibernate.annotation.OneToOne;
import com.bobocode.blyznytsia.bibernate.exception.MalformedEntityException;
import java.lang.reflect.Field;
import java.util.Arrays;
import lombok.experimental.UtilityClass;

/**
 * Utility class for working with relation fields.
 */
@UtilityClass
public class RelationsUtil {

  /**
   * Checks provided field is relation.
   *
   * @param entityField the entity field to check
   * @return true if the provided field is relation
   */
  public static boolean isRelationField(Field entityField) {
    return entityField.isAnnotationPresent(ManyToOne.class)
        || entityField.isAnnotationPresent(OneToMany.class)
        || entityField.isAnnotationPresent(OneToOne.class);
  }

  /**
   * Checks provided join column name is not null or blank.
   *
   * @param joinColumnName the join column name to check
   * @param entityFieldName the entity field name for which join column should be set
   * @throws MalformedEntityException when join column name is null or empty
   */
  public static void verifyJoinColumnNameIsNotEmpty(String joinColumnName, String entityFieldName) {
    if (joinColumnName == null || joinColumnName.isBlank()) {
      throw new MalformedEntityException("Join column name must be set for field %s"
          .formatted(entityFieldName));
    }
  }

  /**
   * Checks provided mapped by field is not null or blank.
   *
   * @param mappedBy the mapped by field in child entity
   * @param entityFieldName the entity field name for which mapped by field should be set
   * @throws MalformedEntityException when mapped by field is null or empty
   */
  public static void verifyMappedByIsNotEmpty(String mappedBy, String entityFieldName) {
    if (mappedBy == null || mappedBy.isBlank()) {
      throw new MalformedEntityException("Mapped by must be set for field %s".formatted(entityFieldName));
    }
  }

  /**
   * Returns specified mapped by {@link Field} in child class.
   *
   * @param entityFieldType the entity field class
   * @param mappedBy the mapped by field in child entity
   * @return specified mapped by {@link Field} in child class
   * @throws MalformedEntityException when specified mapped by field was not found in child class
   */
  public static Field getMappedByFieldInChildClass(Class<?> entityFieldType, String mappedBy) {
    return Arrays.stream(entityFieldType.getDeclaredFields())
        .filter(field -> field.getName().equals(mappedBy))
        .findFirst()
        .orElseThrow(() -> new MalformedEntityException(
            "Field %s that was specified in mapped by in parent class doesn't exist in class %s"
                .formatted(mappedBy, entityFieldType.getName())));
  }

}
