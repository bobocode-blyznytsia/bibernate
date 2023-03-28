package com.bobocode.blyznytsia.bibernate.util;

import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityNonIdFields;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveEntityIdField;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveEntityTableName;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveFieldColumnName;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Field;
import lombok.experimental.UtilityClass;

/**
 * Utility class for generating SQL statements
 */
@UtilityClass
public class SqlUtil {

  private static final String SELECT_STATEMENT_TEMPLATE = "SELECT * FROM %s WHERE %s = ?";
  private static final String INSERT_STATEMENT_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";
  private static final String UPDATE_STATEMENT_TEMPLATE = "UPDATE %s SET %s WHERE %s = ?";
  private static final String DELETE_STATEMENT_TEMPLATE = "DELETE FROM %s WHERE %s = ?";

  /**
   * Builds an SQL INSERT statement for the given entity type.
   *
   * @param entityType the entity type for which to build the statement
   * @return the SQL INSERT statement
   */
  public static String buildInsertStatement(Class<?> entityType) {
    var commaSeparatedColumns = getEntityNonIdFields(entityType).stream()
        .map(EntityUtil::resolveFieldColumnName)
        .collect(joining(", "));
    var commaSeparatedWildcards = getEntityNonIdFields(entityType).stream()
        .map(f -> "?")
        .collect(joining(", "));
    var tableName = resolveEntityTableName(entityType);
    return INSERT_STATEMENT_TEMPLATE.formatted(tableName, commaSeparatedColumns, commaSeparatedWildcards);
  }

  /**
   * Builds an SQL SELECT statement for the given entity type and key field.
   *
   * @param entityType the entity type for which to build the statement
   * @param key        the key field used for the WHERE clause
   * @return the SQL SELECT statement
   */
  public static String buildSelectStatement(Class<?> entityType, Field key) {
    var tableName = resolveEntityTableName(entityType);
    var searchColumnName = resolveFieldColumnName(key);
    return SELECT_STATEMENT_TEMPLATE.formatted(tableName, searchColumnName);
  }

  /**
   * Builds an SQL DELETE statement for the given entity type.
   *
   * @param entityType the entity type for which to build the statement
   * @return the SQL DELETE statement
   */
  public static String buildDeleteStatement(Class<?> entityType) {
    var tableName = resolveEntityTableName(entityType);
    var idFieldName = resolveFieldColumnName(resolveEntityIdField(entityType));
    return DELETE_STATEMENT_TEMPLATE.formatted(tableName, idFieldName);
  }

  /**
   * Builds an SQL UPDATE statement for the given entity type.
   *
   * @param entityType the entity type for which to build the statement
   * @return the SQL UPDATE statement
   */
  public static String buildUpdateStatement(Class<?> entityType) {
    var tableName = resolveEntityTableName(entityType);
    var columnAssignments = getEntityNonIdFields(entityType).stream()
        .map(EntityUtil::resolveFieldColumnName)
        .map(col -> col + " = ?")
        .collect(joining(", "));
    var idFieldName = resolveFieldColumnName(resolveEntityIdField(entityType));
    return UPDATE_STATEMENT_TEMPLATE.formatted(tableName, columnAssignments, idFieldName);
  }
}
