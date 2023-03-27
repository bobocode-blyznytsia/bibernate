package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Indicates that a transaction could not be begun, committed or rolled back.
 */
public class TransactionException extends BibernateException {
  /**
   * Constructs a TransactionException using the specified information.
   *
   * @param message The message explaining the exception condition
   * @param cause The underlying cause
   */
  public TransactionException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a TransactionException using the specified information.
   *
   * @param message The message explaining the exception condition
   */
  public TransactionException(String message) {
    super(message);
  }

}