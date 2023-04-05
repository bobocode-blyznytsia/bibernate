package com.bobocode.blyznytsia.bibernate.exception;

import com.bobocode.blyznytsia.bibernate.query.Query;

/**
 * Exception thrown during {@link Query} execution in case when one or more parameters
 * were specified in the SQL query, but not filled
 */
public class MissingParamsException extends BibernateException {
  public MissingParamsException(String message) {
    super(message);
  }
}
