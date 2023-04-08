package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Base class for exceptions in Bibernate framework
 */
public class BibernateException extends RuntimeException {

  public BibernateException(String message, Throwable cause) {
    super(message, cause);
  }

  public BibernateException(String message) {
    super(message);
  }

}
