package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Exception for errors that occur during Repository generation
 */
public class RepositoryGenerationException extends BibernateException {
  public RepositoryGenerationException(String message, Throwable cause) {
    super(message, cause);
  }

  public RepositoryGenerationException(String message) {
    super(message);
  }
}
