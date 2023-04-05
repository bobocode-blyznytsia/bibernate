package com.bobocode.blyznytsia.bibernate.util;

import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveFieldColumnName;

import com.bobocode.blyznytsia.bibernate.exception.ResultSetMappingException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.experimental.UtilityClass;

/**
 * Utility class for working with {@link ResultSet}.
 */
@UtilityClass
public class ResultSetUtil {

  /**
   * Returns value of specified column name from database.
   *
   * @param rs instance of {@link ResultSet}
   * @param columnName the column name in database
   * @return value of specified column name from database
   * @throws ResultSetMappingException when the column name is not valid
   */
  public static Object getValueFromResultSet(ResultSet rs, String columnName) {
    try {
      return rs.getObject(columnName);
    } catch (SQLException ex) {
      throw new ResultSetMappingException(columnName, ex);
    }
  }

  /**
   * Returns entity id value from {@link ResultSet}
   *
   * @param rs instance of {@link ResultSet}
   * @param entityType the entity class
   * @return entity id value from {@link ResultSet}
   */
  public static Object getEntityIdValueFromResultSet(ResultSet rs, Class<?> entityType) {
    Field idField = EntityUtil.resolveEntityIdField(entityType);
    String idDbColumnName = resolveFieldColumnName(idField);
    return getValueFromResultSet(rs, idDbColumnName);
  }

}
