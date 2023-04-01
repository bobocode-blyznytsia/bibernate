package com.bobocode.blyznytsia.bibernate.util;

import static org.junit.jupiter.api.Assertions.*;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import com.bobocode.blyznytsia.bibernate.exception.MalformedEntityException;
import com.bobocode.blyznytsia.bibernate.testdata.AnnotatedSampleEntity;
import com.bobocode.blyznytsia.bibernate.testdata.EntityWIthMultipleNonIdFields;
import com.bobocode.blyznytsia.bibernate.testdata.EntityWithMultipleIds;
import com.bobocode.blyznytsia.bibernate.testdata.EntityWithoutId;
import com.bobocode.blyznytsia.bibernate.testdata.IdOnlyEntity;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(CamelCaseNameGenerator.class)
class EntityUtilTest {

  @Nested
  class ResolveEntityTableName {
    @Test
    void resolvesNameWithoutAnnotation() {
      assertEquals("sample_entity", EntityUtil.resolveEntityTableName(SampleEntity.class));
    }

    @Test
    void resolvesNameWithAnnotation() {
      assertEquals("custom_entity_table_name", EntityUtil.resolveEntityTableName(AnnotatedSampleEntity.class));
    }
  }

  @Nested
  class ResolveEntityColumnName {
    @Test
    void resolvesNameWithoutAnnotation() throws NoSuchFieldException {
      var field = SampleEntity.class.getDeclaredField("someValue");
      assertEquals("some_value", EntityUtil.resolveFieldColumnName(field));
    }

    @Test
    void resolvesNameWithAnnotation() throws NoSuchFieldException {
      var field = AnnotatedSampleEntity.class.getDeclaredField("someValue");
      assertEquals("custom_column_name", EntityUtil.resolveFieldColumnName(field));
    }
  }

  @Nested
  class ResolveEntityIdField {
    @Test
    void resolvesIdField() throws NoSuchFieldException {
      var expectedIdField = SampleEntity.class.getDeclaredField("id");
      assertEquals(expectedIdField, EntityUtil.resolveEntityIdField(SampleEntity.class));
    }

    @Test
    void throwsMalformedentityexceptionWhenNoIdFound() {
      var expectedErrMessage = "Entity of type EntityWithoutId must contain a primary key field annotated with @Id";
      var exception =
          assertThrows(MalformedEntityException.class,
              () -> EntityUtil.resolveEntityIdField(EntityWithoutId.class));
      assertEquals(expectedErrMessage, exception.getMessage());
    }

    @Test
    void throwsMalformedentityexceptionWhenMultipleIdFound() {
      var expectedErrMessage = "Entity of type EntityWithMultipleIds must contain only one @Id field";
      var exception = assertThrows(MalformedEntityException.class,
          () -> EntityUtil.resolveEntityIdField(EntityWithMultipleIds.class));
      assertEquals(expectedErrMessage, exception.getMessage());
    }

  }

  @Nested
  class GetEntityNonIdFields {
    @Test
    void returnsListOfFields() {
      var expectedFields = Arrays.stream(EntityWIthMultipleNonIdFields.class.getDeclaredFields())
          .filter(field -> !field.getName().equals("id"))
          .toList();
      var actualNonIdFields = EntityUtil.getEntityNonIdFields(EntityWIthMultipleNonIdFields.class);
      assertEquals(expectedFields, actualNonIdFields);
    }

    @Test
    void returnsEmptyListIfNoFieldsFound() {
      var nonIdFields = EntityUtil.getEntityNonIdFields(IdOnlyEntity.class);
      assertTrue(nonIdFields.isEmpty());
    }

  }

}
