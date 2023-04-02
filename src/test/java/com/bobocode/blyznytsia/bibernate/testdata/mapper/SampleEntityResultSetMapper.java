package com.bobocode.blyznytsia.bibernate.testdata.mapper;

import com.bobocode.blyznytsia.bibernate.testdata.entity.SampleEntity;
import com.bobocode.blyznytsia.bibernate.util.ResultSetMapper;
import java.sql.ResultSet;
import lombok.SneakyThrows;

public class SampleEntityResultSetMapper implements ResultSetMapper {
  @Override
  @SneakyThrows
  public <T> T mapToEntity(ResultSet rs, Class<T> entityType) {
    return (T) new SampleEntity(rs.getLong("id"),rs.getString("some_value"));
  }
}
