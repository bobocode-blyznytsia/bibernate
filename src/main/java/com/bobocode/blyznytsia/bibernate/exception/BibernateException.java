package com.bobocode.blyznytsia.bibernate.exception;

public class BibernateException extends RuntimeException {
  public BibernateException(String message, Throwable cause) {
    super(message, cause);
  }

  public BibernateException(String message) {
    super(message);
  }

}
