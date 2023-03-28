package com.bobocode.blyznytsia.bibernate.lambda;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A function that accepts an instance opened {@link PreparedStatement} and returns a generic value, or throws
 * {@link SQLException}
 *
 * @param <T> Entity to be returned
 */
@FunctionalInterface
public interface StatementFunction<T> {
  T apply(PreparedStatement stmt) throws SQLException;
}