package com.bobocode.blyznytsia.bibernate.mapper;

import java.sql.ResultSet;

/**
 * The interface consists methods for mapping instance of {@link ResultSet} to entity.
 * Supported mappings relation types: one-to-one, many-to-one and one-to-many.
 * All relations are eagerly fetched.
 */
public interface ResultSetMapper {

  /**
   * Maps {@link ResultSet} to entity.
   *
   * @param resultSet     instance of {@link ResultSet}
   * @param entityClass   the type of entity to map
   * @return an entity instance
   */
  <T> T mapToEntity(ResultSet resultSet, Class<T> entityClass);

}
