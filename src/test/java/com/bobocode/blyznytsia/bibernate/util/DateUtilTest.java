package com.bobocode.blyznytsia.bibernate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bobocode.blyznytsia.bibernate.exception.NotSupportedException;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.lang.reflect.Field;

class DateUtilTest {

  @Test
  void isDateFieldWithSupportedDateTypes() {
    assertTrue(DateUtil.isDateField(LocalDateTime.class));
    assertTrue(DateUtil.isDateField(LocalDate.class));
    assertTrue(DateUtil.isDateField(LocalTime.class));
    assertTrue(DateUtil.isDateField(Instant.class));
  }

  @Test
  void isDateFieldWithUnSupportedDateTypes() {
    assertThrows(NotSupportedException.class, () -> DateUtil.isDateField(ZonedDateTime.class));
    assertThrows(NotSupportedException.class, () -> DateUtil.isDateField(java.util.Date.class));
    assertThrows(NotSupportedException.class, () -> DateUtil.isDateField(java.util.Calendar.class));
    assertThrows(NotSupportedException.class, () -> DateUtil.isDateField(java.sql.Date.class));
    assertThrows(NotSupportedException.class, () -> DateUtil.isDateField(java.sql.Timestamp.class));
    assertThrows(NotSupportedException.class, () -> DateUtil.isDateField(java.sql.Time.class));
  }

  @Test
  void isDateFieldWithUnSupportedOtherTypes() {
    assertFalse(DateUtil.isDateField(String.class));
  }

  @Test
  void getDateFieldValueForLocalDateField() throws Exception {
    Field localDateField = TestEntity.class.getDeclaredField("localDateField");
    java.sql.Date date = java.sql.Date.valueOf("2023-01-01");
    Object convertedValue = DateUtil.getDateFieldValue(localDateField, date);
    assertTrue(convertedValue instanceof LocalDate);
    assertEquals(LocalDate.parse("2023-01-01"), convertedValue);
  }

  @Test
  void getDateFieldValueForLocalDateFieldFromTimestamp() throws Exception {
    Field localDateField = TestEntity.class.getDeclaredField("localDateField");
    Timestamp timestamp = Timestamp.valueOf("2023-01-01 12:00:00");
    Object convertedValue = DateUtil.getDateFieldValue(localDateField, timestamp);
    assertTrue(convertedValue instanceof LocalDate);
    assertEquals(LocalDate.parse("2023-01-01"), convertedValue);
  }

  @Test
  void getDateFieldValueForLocalDateTimeField() throws Exception {
    Field localDateTimeField = TestEntity.class.getDeclaredField("localDateTimeField");
    Timestamp timestamp = Timestamp.valueOf("2023-01-01 12:00:00");
    Object convertedValue = DateUtil.getDateFieldValue(localDateTimeField, timestamp);
    assertTrue(convertedValue instanceof LocalDateTime);
    assertEquals(LocalDateTime.parse("2023-01-01T12:00:00"), convertedValue);
  }

  @Test
  void getDateFieldValueForLocalTimeField() throws Exception {
    Field localTimeField = TestEntity.class.getDeclaredField("localTimeField");
    Timestamp timestamp = Timestamp.valueOf("2023-01-01 09:30:00");
    Object convertedValue = DateUtil.getDateFieldValue(localTimeField, timestamp);
    assertTrue(convertedValue instanceof LocalTime);
    assertEquals(LocalTime.parse("09:30:00"), convertedValue);
  }

  @Test
  void getDateFieldValueForInstantField() throws Exception {
    Field instantField = TestEntity.class.getDeclaredField("instantField");
    Timestamp timestamp = Timestamp.from(Instant.parse("2023-01-01T12:00:00Z"));
    Object convertedValue = DateUtil.getDateFieldValue(instantField, timestamp);
    assertTrue(convertedValue instanceof Instant);
    assertEquals(Instant.parse("2023-01-01T12:00:00Z"), convertedValue);
  }

  @Test
  void getDateFieldValueForUnsupportedFieldType() throws Exception {
    Field unsupportedField = TestEntity.class.getDeclaredField("unsupportedField");
    Timestamp timestamp = Timestamp.valueOf("2022-01-01 12:00:00");
    assertThrows(NotSupportedException.class, () -> DateUtil.getDateFieldValue(unsupportedField, timestamp));
  }

  @Test
  void getDateFieldValueForUnsupportedSqlType() throws Exception {
    Field unsupportedSqlTypeField = TestEntity.class.getDeclaredField("unsupportedSqlTypeField");
    Object unsupportedSqlTypeValue = new Object();
    assertThrows(NotSupportedException.class, () -> DateUtil.getDateFieldValue(unsupportedSqlTypeField, unsupportedSqlTypeValue));
  }

  private static class TestEntity {
    private LocalDate localDateField;
    private LocalDateTime localDateTimeField;
    private LocalTime localTimeField;
    private Instant instantField;
    private String unsupportedField;
    private java.sql.Time unsupportedSqlTypeField;
  }
}

