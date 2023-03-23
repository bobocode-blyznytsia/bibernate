package com.bobocode.blyznytsia.bibernate.util;

import java.sql.ResultSet;

public interface ResultSetMapper {
  <T> T mapToEntity(ResultSet rs, Class<T> entityType);

}
