package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Exception thrown on errors during execution of {@see BibernateReadonlyRepository} or its sub-interfaces
 */
public class RepositoryException extends BibernateException {
  public RepositoryException(String message) {
    super(message);
  }
}
