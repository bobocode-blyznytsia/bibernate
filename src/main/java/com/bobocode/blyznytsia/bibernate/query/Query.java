package com.bobocode.blyznytsia.bibernate.query;

import com.bobocode.blyznytsia.bibernate.exception.MissingParamsException;
import java.util.List;
import java.util.Optional;

/**
 * The interface provides a comprehensive api to retrieve an entity using native queries
 *
 * @param <T> the type of the entity
 */
public interface Query<T> {

  /**
   * Sets the value of the named parameter.
   *
   * @param namedParam the name of the parameter
   * @param value      the parameter value
   * @throws IllegalArgumentException if the named parameter does not exist in the query
   */
  Query<T> setParam(String namedParam, Object value);

  /**
   * Sets the value of the ordinal (positional) parameter.
   *
   * @param ordinalParamIndex the index of the parameter
   * @param value             the parameter value
   * @throws IllegalArgumentException if the parameter with the specified index does not exist in the query
   */
  Query<T> setParam(int ordinalParamIndex, Object value);

  /**
   * Executes the query and returns a single result.
   *
   * @return an {@code Optional} containing the result, or an empty {@code Optional} if there is no result
   * @throws MissingParamsException if any of the query parameters were not set
   */
  Optional<T> getSingleResult();

  /**
   * Executes the query and returns a list of results.
   *
   * @return a {@code List} of results, or an empty {@code List} if there are no results
   * @throws MissingParamsException if any of the query parameters were not set
   */
  List<T> getResultList();
}
