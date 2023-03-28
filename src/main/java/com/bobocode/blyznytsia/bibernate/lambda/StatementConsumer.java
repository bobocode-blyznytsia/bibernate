package com.bobocode.blyznytsia.bibernate.lambda;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A narrowed version of {@link StatementFunction} that consumes {@link PreparedStatement} with no return type
 */
@FunctionalInterface
public interface StatementConsumer extends StatementFunction<Void> {
  default Void apply(PreparedStatement stmt) throws SQLException {
    consume(stmt);
    return null;
  }


  void consume(PreparedStatement stmt) throws SQLException;
}
