package com.bobocode.blyznytsia.bibernate.transaction;

/**
 * Interface used to control transactions on resource-local entity managers.
 */
public interface Transaction {

  /**
   * Start a resource transaction.
   *
   * @throws IllegalStateException if <code>isActive()</code> is true
   */
  void begin();

  /**
   * Commit the current resource transaction.
   *
   * @throws IllegalStateException if <code>isActive()</code> is false
   */
  void commit();

  /**
   * Roll back the current resource transaction.
   *
   * @throws IllegalStateException if <code>isActive()</code> is false
   */
  void rollback();

  /**
   * Indicate whether a resource transaction is in progress.
   *
   * @return boolean indicating whether transaction is in progress
   */
  boolean isActive();

}