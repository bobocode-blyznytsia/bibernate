package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Exception for errors that occur during parsing and mapping {@link java.sql.ResultSet}.
 */
public class ResultSetMappingException extends BibernateException {

  public ResultSetMappingException(String columnName, Throwable cause) {
    super(String.format("Failed to map ResultSet to entity object. "
        + "Cannot find column with name %s.", columnName), cause);
  }
}
