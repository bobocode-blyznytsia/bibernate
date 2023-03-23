package com.bobocode.blyznytsia.bibernate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CaseUtilTest {
  @Test
  void camelToSnakeCaseConvertingFieldNames() {
    assertEquals("some_field",                   CaseUtil.camelToSnakeCase("someField"));
    assertEquals("some_field_but_longer",        CaseUtil.camelToSnakeCase("someFieldButLonger"));
    assertEquals("field_uppercase_abbreviation", CaseUtil.camelToSnakeCase("fieldUppercaseABBREVIATION"));
  }

  void camelToSnakeCaseConvertingClassNames(){
    assertEquals("some_class",                   CaseUtil.camelToSnakeCase("SomeClass"));
    assertEquals("some_class_but_longer",        CaseUtil.camelToSnakeCase("SomeClassButLonger"));
    assertEquals("class_uppercase_abbreviation", CaseUtil.camelToSnakeCase("ClassUppercaseABBREVIATION"));
  }

}