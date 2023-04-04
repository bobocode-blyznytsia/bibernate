package com.bobocode.blyznytsia.bibernate.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bobocode.blyznytsia.bibernate.annotation.ManyToOne;
import com.bobocode.blyznytsia.bibernate.annotation.OneToMany;
import com.bobocode.blyznytsia.bibernate.annotation.OneToOne;
import com.bobocode.blyznytsia.bibernate.exception.MalformedEntityException;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RelationsUtilTest {
  private Field manyToOneField;
  private Field oneToManyField;
  private Field oneToOneField;
  private Field nonRelationField;

  @BeforeEach
  void setUp() throws NoSuchFieldException {
    manyToOneField = TestEntity.class.getDeclaredField("manyToOneField");
    oneToManyField = TestEntity.class.getDeclaredField("oneToManyField");
    oneToOneField = TestEntity.class.getDeclaredField("oneToOneField");
    nonRelationField = TestEntity.class.getDeclaredField("nonRelationField");
  }

  @Test
  void isRelationField() {
    assertTrue(RelationsUtil.isRelationField(manyToOneField));
    assertTrue(RelationsUtil.isRelationField(oneToManyField));
    assertTrue(RelationsUtil.isRelationField(oneToOneField));
    assertFalse(RelationsUtil.isRelationField(nonRelationField));
  }

  @Test
  void verifyJoinColumnNameIsNotEmptyWithNotEmptyJoinColumnName() {
    assertDoesNotThrow(() -> RelationsUtil.verifyJoinColumnNameIsNotEmpty("columnName", "entityFieldName"));
  }

  @Test
  void verifyJoinColumnNameIsNotEmptyWithEmptyJoinColumnName() {
    assertThrows(MalformedEntityException.class,
        () -> RelationsUtil.verifyJoinColumnNameIsNotEmpty("", "entityFieldName"));
  }

  @Test
  void verifyJoinColumnNameIsNotEmptyWithNullJoinColumnName() {
    assertThrows(MalformedEntityException.class,
        () -> RelationsUtil.verifyJoinColumnNameIsNotEmpty(null, "entityFieldName"));
  }

  @Test
  void verifyMappedByIsNotEmptyWithNotEmptyMappedBy() {
    assertDoesNotThrow(() -> RelationsUtil.verifyMappedByIsNotEmpty("mappedBy", "entityFieldName"));
  }

  @Test
  void verifyMappedByIsNotEmptyWithEmptyMappedBy() {
    assertThrows(MalformedEntityException.class,
        () -> RelationsUtil.verifyMappedByIsNotEmpty("", "entityFieldName"));
  }

  @Test
  void verifyMappedByIsNotEmptyWithNullMappedBy() {
    assertThrows(MalformedEntityException.class,
        () -> RelationsUtil.verifyMappedByIsNotEmpty(null, "entityFieldName"));
  }

  @Test
  void getMappedByFieldInChildClassWithExistingField() {
    Field mappedByField = RelationsUtil.getMappedByFieldInChildClass(TestEntity.class, "nonRelationField");
    assertNotNull(mappedByField);
    assertEquals("nonRelationField", mappedByField.getName());
  }

  @Test
  void getMappedByFieldInChildClassWithNotExistingField() {
    assertThrows(MalformedEntityException.class,
        () -> RelationsUtil.getMappedByFieldInChildClass(TestEntity.class, "nonExistingField"));
  }

  private static class TestEntity {
    @ManyToOne(joinColumnName = "test")
    private Object manyToOneField;

    @OneToMany(mappedBy = "test")
    private Object oneToManyField;

    @OneToOne
    private Object oneToOneField;

    private Object nonRelationField;
  }
}