package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Exception class for errors that occur when an entity is not properly formed
 */
public class MalformedEntityException extends BibernateException {
  public MalformedEntityException(String message) {
    super(message);
  }
}
