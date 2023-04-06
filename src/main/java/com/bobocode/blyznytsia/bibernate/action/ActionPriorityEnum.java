package com.bobocode.blyznytsia.bibernate.action;

/**
 * Represents {@link EntityAction} priority
 */
public enum ActionPriorityEnum {
  INSERT_PRIORITY(1),
  UPDATE_PRIORITY(2),
  DELETE_PRIORITY(3);

  private final int priorityNumber;

  ActionPriorityEnum(int priorityNumber) {
    this.priorityNumber = priorityNumber;
  }

  public int getPriorityNumber() {
    return priorityNumber;
  }
}

