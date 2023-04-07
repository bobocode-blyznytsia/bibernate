package com.bobocode.blyznytsia.bibernate.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(CamelCaseNameGenerator.class)
class QueryBuilderTest {
  private QueryBuilder queryBuilder;

  @BeforeEach
  void setUp() {
    queryBuilder = new QueryBuilder("users");
  }

  @Test
  void buildsQueryWithNoExtraParams() {
    var expectedSqlQuery = "SELECT * FROM users";
    var actualSqlQuery = queryBuilder.buildNativeSqlQuery();
    assertEquals(expectedSqlQuery, actualSqlQuery);
  }

  @Test
  void buildsQueryWithWhereClauseParams() {
    queryBuilder
        .isEqual("age")
        .and()
        .isNull("email")
        .or()
        .isEqual("country");
    var expectedSqlQuery = "SELECT * FROM users WHERE age = ?1 AND email IS NULL OR country = ?2";
    var actualSqlQuery = queryBuilder.buildNativeSqlQuery();
    assertEquals(expectedSqlQuery, actualSqlQuery);
  }

  @Test
  void buildsQueryWithOrderClauseParams() {
    queryBuilder
        .orderBy("age")
        .descending();
    var expectedSqlQuery = "SELECT * FROM users ORDER BY age DESC";
    var actualSqlQuery = queryBuilder.buildNativeSqlQuery();
    assertEquals(expectedSqlQuery, actualSqlQuery);
  }

  @Test
  void buildsQueryWithBothWhereAndOrderClauseParams() {
    queryBuilder
        .isEqual("age")
        .and()
        .isNotNull("email")
        .or()
        .isEqual("country")
        .orderBy("age")
        .descending();
    var expectedSqlQuery = "SELECT * FROM users WHERE age = ?1 AND email IS NOT NULL OR country = ?2 ORDER BY age DESC";
    var actualSqlQuery = queryBuilder.buildNativeSqlQuery();
    assertEquals(expectedSqlQuery, actualSqlQuery);
  }


}