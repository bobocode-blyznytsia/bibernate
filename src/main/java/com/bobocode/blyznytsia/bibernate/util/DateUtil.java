package com.bobocode.blyznytsia.bibernate.util;

import com.bobocode.blyznytsia.bibernate.exception.NotSupportedException;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for working with date types.
 */
@UtilityClass
@Slf4j
public class DateUtil {

  private static final String JAVA_TIME_PACKAGE = "java.time";
  private static final String JAVA_UTIL_DATE = "java.util.Date";
  private static final String JAVA_UTIL_CALENDAR = "java.util.Calendar";
  private static final String JAVA_SQL_DATE = "java.sql.Date";
  private static final String JAVA_SQL_TIMESTAMP = "java.sql.Timestamp";
  private static final String JAVA_SQL_TIME = "java.sql.Time";

  /**
   * Checks provided field type is date and whether it is supported by Bibernate framework.
   * Supported field types: {@link LocalDateTime}, {@link LocalDate}, {@link LocalTime},
   * {@link Instant} from <code>java.time</code> package.
   *
   * @param fieldType the entity field type to check
   * @return true if the provided field type is date, and it is supported by Bibernate framework
   * @throws NotSupportedException when the date field type is not supported by Bibernate framework
   */
  public static boolean isDateField(Class<?> fieldType) {
    if (isDateField(fieldType.getName())) {
      return verifyTheFieldTypeIsSupported(fieldType);
    } else {
      return false;
    }
  }

  private boolean isDateField(String fieldType) {
    return fieldType.contains(JAVA_TIME_PACKAGE) || fieldType.contains(JAVA_UTIL_DATE)
        || fieldType.contains(JAVA_UTIL_CALENDAR) || fieldType.contains(JAVA_SQL_DATE)
        || fieldType.contains(JAVA_SQL_TIMESTAMP) || fieldType.contains(JAVA_SQL_TIME);
  }

  /**
   * Converts provided value from database to corresponding field date type. Supported field types:
   * {@link LocalDateTime}, {@link LocalDate}, {@link LocalTime}, {@link Instant} from
   * <code>java.time</code> package. Supported sql types: <code>DATE</code> and
   * <code>TIMESTAMP</code>.
   *
   * @param entityField the entity date field
   * @param value       the value from database
   * @return converted value to corresponding field date type
   * @throws NotSupportedException when the date field type or sql type is not supported by
   *                               Bibernate framework
   */
  public static Object getDateFieldValue(Field entityField, Object value) {
    log.debug("Convert date field '{}' to set field type {} with value {}.", entityField.getName(),
        entityField.getType(), value);
    return switch (value) {
      case Timestamp ignore -> getFieldValueForLocalDateTimeDependsOnFieldType(
          entityField.getType(), ((Timestamp) value));
      case Date ignore -> ((java.sql.Date) value).toLocalDate();
      default -> throw NotSupportedException.sqlTypeIsNotSupported(entityField.getName());
    };
  }

  private static Object getFieldValueForLocalDateTimeDependsOnFieldType(Class<?> fieldType,
      Timestamp timestamp) {
    if (fieldType.equals(LocalDateTime.class)) {
      return timestamp.toLocalDateTime();
    } else if (fieldType.equals(LocalDate.class)) {
      return timestamp.toLocalDateTime().toLocalDate();
    } else if (fieldType.equals(LocalTime.class)) {
      return timestamp.toLocalDateTime().toLocalTime();
    } else if (fieldType.equals(Instant.class)) {
      return timestamp.toInstant();
    } else {
      throw NotSupportedException.fieldTypeIsNotSupported(fieldType);
    }
  }

  private static boolean verifyTheFieldTypeIsSupported(Class<?> fieldType) {
    if (fieldType.equals(LocalDateTime.class) || fieldType.equals(LocalDate.class)
        || fieldType.equals(LocalTime.class) || fieldType.equals(Instant.class)) {
      return true;
    } else {
      throw NotSupportedException.fieldTypeIsNotSupported(fieldType);
    }
  }

}
