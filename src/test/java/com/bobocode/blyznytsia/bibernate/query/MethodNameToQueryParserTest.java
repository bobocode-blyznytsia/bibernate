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
  @MethodSource("com.bobocode.blyznytsia.bibernate.query.MethodNameToQueryParserTest#testData")
  void buildsQueryFromMethodName(Map.Entry<String,String> entry) {
    assertEquals(entry.getValue(), new MethodNameToQueryParser(Person.class).generateQueryByName(entry.getKey()));
  }

  private static Set<Map.Entry<String, String>> testData(){
    return Map.of(
        "findOneByName",
        "SELECT * FROM person WHERE name = ?1",

        "findOneByNameIs",
        "SELECT * FROM person WHERE name = ?1",

        "findOneByNameEquals",
        "SELECT * FROM person WHERE name = ?1",

        "findOneByNameAndStatus",
        "SELECT * FROM person WHERE name = ?1 AND status = ?2",

        "findOneByNameOrStatus",
        "SELECT * FROM person WHERE name = ?1 OR status = ?2",

        "findAllByNameIsNull",
        "SELECT * FROM person WHERE name IS NULL",

        "findAllByNameIsNotNull",
        "SELECT * FROM person WHERE name IS NOT NULL",

        "findAllByNameNull",
        "SELECT * FROM person WHERE name IS NULL",

        "findAllByNameNotNull",
        "SELECT * FROM person WHERE name IS NOT NULL",

        "findAllByNameOrderByStatusDesc",
        "SELECT * FROM person WHERE name = ?1 ORDER BY status DESC"
    ).entrySet();
  }

}
