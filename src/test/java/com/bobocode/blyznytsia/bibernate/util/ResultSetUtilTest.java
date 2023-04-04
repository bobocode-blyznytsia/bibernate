package com.bobocode.blyznytsia.bibernate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.exception.ResultSetMappingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResultSetUtilTest {

  @Mock
  private ResultSet resultSet;

  @Test
  void getValueFromResultSet() throws SQLException {
    final String columnName = "my_column_name";
    final Object expectedValue = "expected_value";
    when(resultSet.getObject(columnName)).thenReturn(expectedValue);
    final Object result = ResultSetUtil.getValueFromResultSet(resultSet, columnName);
    assertEquals(expectedValue, result);
  }

  @Test
  void getValueFromResultSetWithInvalidColumnName() throws SQLException {
    final String invalidColumnName = "invalid_column_name";
    when(resultSet.getObject(invalidColumnName)).thenThrow(SQLException.class);
    assertThrows(ResultSetMappingException.class,
        () -> ResultSetUtil.getValueFromResultSet(resultSet, invalidColumnName));
  }

  @Test
  void getEntityIdValueFromResultSet() throws SQLException, SecurityException {
    final long expectedId = 42L;
    final Class<?> entityClass = MyClass.class;
    final String idColumnName = "id";
    when(resultSet.getObject(idColumnName)).thenReturn(expectedId);
    final Object result = ResultSetUtil.getEntityIdValueFromResultSet(resultSet, entityClass);
    assertEquals(expectedId, result);
  }

  private static class MyClass {
    @Id
    private Long id;
  }
}

