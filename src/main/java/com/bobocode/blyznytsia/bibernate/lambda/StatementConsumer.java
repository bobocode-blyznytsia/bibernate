package com.bobocode.blyznytsia.bibernate.lambda;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A narrowed version of {@link StatementFunction} that consumes {@link PreparedStatement} with no return type
 */
@FunctionalInterface
public interface StatementConsumer extends StatementFunction<Void> {

  /**
   * Default implementation for a @{@link StatementFunction#apply(PreparedStatement)} method that woks as an adapter
   * for method {@link StatementConsumer#consume(PreparedStatement)}
   *
   * @param stmt - an instance of {@link PreparedStatement}
   */
  default Void apply(PreparedStatement stmt) throws SQLException {
    consume(stmt);
    return null;
  }


  /**
   * Method that accepts {@link PreparedStatement} and perform operations with it
   *
   * @param stmt - an instance of {@link PreparedStatement}
   * @throws SQLException - exception that may occur performing operation within {@link PreparedStatement}
   */
  void consume(PreparedStatement stmt) throws SQLException;
}
