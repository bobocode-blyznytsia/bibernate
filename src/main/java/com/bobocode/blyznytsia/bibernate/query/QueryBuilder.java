package com.bobocode.blyznytsia.bibernate.query;

/**
 * A class that provides an API for building SQL queries.
 */
public class QueryBuilder {
  private static final String WHITESPACE = " ";
  private final String tableName;
  private final StringBuilder whereClauseBuilder = new StringBuilder();
  private final StringBuilder orderClauseBuilder = new StringBuilder();
  private int ordinalParamIdx = 1;

  public QueryBuilder(String tableName) {
    this.tableName = tableName;
  }

  public QueryBuilder isEqual(String columnName) {
    return addWhereAndReturn(columnName + " = ?" + ordinalParamIdx++);
  }

  public QueryBuilder and() {
    return addWhereAndReturn("AND");
  }

  public QueryBuilder or() {
    return addWhereAndReturn("OR");
  }

  public QueryBuilder isNull(String columnName) {
    return addWhereAndReturn(columnName + " IS NULL");
  }

  public QueryBuilder isNotNull(String columnName) {
    return addWhereAndReturn(columnName + " IS NOT NULL");
  }

  public QueryBuilder orderBy(String columnName) {
    return addOrderAndReturn(columnName);
  }

  public QueryBuilder descending() {
    return addOrderAndReturn("DESCENDING");
  }

  public QueryBuilder addWhereAndReturn(String clause) {
    whereClauseBuilder.append(WHITESPACE).append(clause);
    return this;
  }

  public QueryBuilder addOrderAndReturn(String clause) {
    orderClauseBuilder.append(WHITESPACE).append(clause);
    return this;
  }

  public String buildNativeSqlQuery() {
    var queryBuilder = new StringBuilder("SELECT * FROM ")
        .append(tableName);
    if (!whereClauseBuilder.isEmpty()) {
      queryBuilder
          .append(" WHERE")
          .append(whereClauseBuilder);
    }
    if (!orderClauseBuilder.isEmpty()) {
      queryBuilder
          .append(" ORDER BY")
          .append(orderClauseBuilder);
    }
    return queryBuilder.toString();
  }

}
