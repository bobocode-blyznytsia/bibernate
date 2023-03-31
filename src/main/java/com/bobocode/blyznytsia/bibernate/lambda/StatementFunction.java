package com.bobocode.blyznytsia.bibernate.lambda;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The functional interface for performing operations with {@link java.sql.ResultSet} and return entity
 *
 * @param <T> Type of entity
 */
@FunctionalInterface
public interface StatementFunction<T> {

  /**
   * Function that accepts an opened instance of {@link PreparedStatement} and returns an entity of type T
   *
   * @param stmt - an instance of {@link PreparedStatement}
   * @return entity of type T
   * @throws SQLException - exception that may occur performing operations with {@link PreparedStatement} within lambda
   */
  T apply(PreparedStatement stmt) throws SQLException;
}
