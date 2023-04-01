package com.bobocode.blyznytsia.bibernate.mapper;

import java.sql.ResultSet;

public interface ResultSetMapper {

  <T> T mapToEntity(ResultSet resultSet, Class<T> entityClass);

}
