package com.bobocode.blyznytsia.bibernate.action;

/**
 * Represents a write SQL operation. Implementation of this interface are used to perform insert, update and remove
 * operations asynchronously.
 */
public interface EntityAction {

  /**
   * Perform insert, update, or remove operations according to the action
   */
  void execute();

  /**
   * Get priority for action
   *
   * @return {@link ActionPriority action priority}
   */
  ActionPriority getPriority();
}

