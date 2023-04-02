package com.bobocode.blyznytsia.bibernate.util;

import static org.junit.jupiter.api.Assertions.*;

import com.bobocode.blyznytsia.bibernate.testdata.entity.EntityWIthMultipleNonIdFields;
import com.bobocode.blyznytsia.bibernate.testdata.entity.SampleEntity;
import org.junit.jupiter.api.Test;

class SqlUtilTest {
  private static final String CORRECT_SELECT_STMT = "SELECT * FROM sample_entity WHERE id = ?";
  private static final String CORRECT_INSERT_STMT = "INSERT INTO entity_with_multiple_non_id_fields "
      + "(first_field, second_field, third_field, fourth_field) VALUES (?, ?, ?, ?)";
  private static final String CORRECT_UPDATE_STMT = "UPDATE entity_with_multiple_non_id_fields "
      + "SET first_field = ?, second_field = ?, third_field = ?, fourth_field = ? WHERE id = ?";
  private static final String CORRECT_DELETE_STMT = "DELETE FROM sample_entity WHERE id = ?";

  @Test
  void buildSelectStatementTest() throws NoSuchFieldException {
    var key = SampleEntity.class.getDeclaredField("id");
    assertEquals(CORRECT_SELECT_STMT, SqlUtil.buildSelectStatement(SampleEntity.class, key));
  }

  @Test
  void buildInsertStatementTest() {
    assertEquals(CORRECT_INSERT_STMT, SqlUtil.buildInsertStatement(EntityWIthMultipleNonIdFields.class));
  }

  @Test
  void buildUpdateStatementTest(){
    assertEquals(CORRECT_UPDATE_STMT, SqlUtil.buildUpdateStatement(EntityWIthMultipleNonIdFields.class));
  }

  @Test
  void buildDeleteStatementTest() {
    assertEquals(CORRECT_DELETE_STMT, SqlUtil.buildDeleteStatement(SampleEntity.class));
  }

}
