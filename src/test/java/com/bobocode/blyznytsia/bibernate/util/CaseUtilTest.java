package com.bobocode.blyznytsia.bibernate.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class CaseUtilTest {

  @ParameterizedTest
  @MethodSource("com.bobocode.blyznytsia.bibernate.util.CaseUtilTest#camelCaseToSnakeCaseValues")
  void camelToSnakeCase(Map.Entry<String,String> entry) {//@Todo: parametrize
    assertEquals(entry.getValue(), CaseUtil.camelToSnakeCase(entry.getKey()));
  }

  static Set<Map.Entry<String, String>> camelCaseToSnakeCaseValues(){
    return Map.of(
        "someField","some_field",
        "someFieldButLonger", "some_field_but_longer",
        "fieldUppercaseABBREVIATION", "field_uppercase_abbreviation",

        "SomeClass", "some_class",
        "SomeClassButLonger","some_class_but_longer",
        "ClassUppercaseABBREVIATION","class_uppercase_abbreviation"
    ).entrySet();
  }
}
