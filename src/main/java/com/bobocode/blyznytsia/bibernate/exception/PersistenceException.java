package com.bobocode.blyznytsia.bibernate.exception;

public class PersistenceException extends BibernateException {
  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

  public PersistenceException(String message) {
    super(message);
  }
}
