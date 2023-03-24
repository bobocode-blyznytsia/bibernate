package com.bobocode.blyznytsia.bibernate.util;

import static org.junit.jupiter.api.Assertions.*;

import com.bobocode.blyznytsia.bibernate.exception.MalformedEntityException;
import com.bobocode.blyznytsia.bibernate.testdata.entity.AnnotatedSampleEntity;
import com.bobocode.blyznytsia.bibernate.testdata.entity.EntityWIthMultipleNonIdFields;
import com.bobocode.blyznytsia.bibernate.testdata.entity.EntityWithNoId;
import com.bobocode.blyznytsia.bibernate.testdata.entity.SampleEntity;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class EntityUtilTest {

  @Test
  void resolveEntityTableNameWithoutAnnotation() {
    assertEquals("sample_entity", EntityUtil.resolveEntityTableName(SampleEntity.class));
  }

  @Test
  void resolveEntityTableNameWithAnnotation() {
    assertEquals("custom_entity_table_name", EntityUtil.resolveEntityTableName(AnnotatedSampleEntity.class));
  }

  @Test
  void resolveFieldColumnValueWithoutAnnotation() throws NoSuchFieldException {
    var field = SampleEntity.class.getDeclaredField("someValue");
    assertEquals("some_value", EntityUtil.resolveFieldColumnName(field));
  }

  @Test
  void resolveFieldColumnValueWithAnnotation() throws NoSuchFieldException {
    var field = AnnotatedSampleEntity.class.getDeclaredField("someValue");
    assertEquals("custom_column_name", EntityUtil.resolveFieldColumnName(field));
  }

  @Test
  void resolveEntityIdField() throws NoSuchFieldException {
    var expectedIdField = SampleEntity.class.getDeclaredField("id");
    assertEquals(expectedIdField, EntityUtil.resolveEntityIdField(SampleEntity.class));
  }

  @Test
  void resolveEntityIdFieldShouldThrowMalformedEntityException() {
    assertThrows(MalformedEntityException.class, () -> EntityUtil.resolveEntityIdField(EntityWithNoId.class));
  }

  @Test
  void getEntityNonIdFields() {
    var expectedField = Arrays.stream(EntityWIthMultipleNonIdFields.class.getDeclaredFields())
        .filter(field -> ! field.getName().equals("id"))
        .toList();
    var actualNonIdFields = EntityUtil.getEntityNonIdFields(EntityWIthMultipleNonIdFields.class);
    assertEquals(expectedField, actualNonIdFields);
  }
}