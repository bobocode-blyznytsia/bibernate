package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Exception for errors that occur during persistence operations within
 * {@link com.bobocode.blyznytsia.bibernate.persister.EntityPersister}
 */
public class PersistenceException extends BibernateException {
  public PersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

  public PersistenceException(String message) {
    super(message);
  }
}
