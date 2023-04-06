package com.bobocode.blyznytsia.bibernate.query;

import static org.junit.jupiter.api.Assertions.*;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import com.bobocode.blyznytsia.bibernate.testdata.Person;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayNameGeneration(CamelCaseNameGenerator.class)
class MethodNameToQueryParserTest {
  @ParameterizedTest
  @MethodSource("com.bobocode.blyznytsia.bibernate.query.MethodQueryGeneratorTest#testData")
  void buildsQueryFromMethodName(Map.Entry<String,String> entry) {
    assertEquals(entry.getValue(), new MethodNameToQueryParser(Person.class).generateQueryByName(entry.getKey()));
  }


  static Set<Map.Entry<String, String>> testData(){
    return Map.of(
        "findByName",
        "SELECT * FROM person WHERE name = ?1",

        "findByNameIs",
        "SELECT * FROM person WHERE name = ?1",

        "findByNameEquals",
        "SELECT * FROM person WHERE name = ?1",

        "findByNameAndStatus",
        "SELECT * FROM person WHERE name = ?1 AND status = ?2",

        "findByNameOrStatus",
        "SELECT * FROM person WHERE name = ?1 OR status = ?2",

        "findByNameIsNull",
        "SELECT * FROM person WHERE name IS NOT NULL",

        "findByNameIsNotNull",
        "SELECT * FROM person WHERE name IS NOT NULL",

        "findByNameNull",
        "SELECT * FROM person WHERE name IS NOT NULL",

        "findByNameNotNull",
        "SELECT * FROM person WHERE name IS NOT NULL",

        "findByNameOrderByStatusDesc",
        "SELECT * FROM person WHERE name = ?1 ORDER BY status DESCENDING"
    ).entrySet();
  }

}