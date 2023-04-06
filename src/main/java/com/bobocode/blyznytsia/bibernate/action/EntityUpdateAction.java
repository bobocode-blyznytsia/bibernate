package com.bobocode.blyznytsia.bibernate.action;

import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityUpdateAction implements EntityAction {
  private final Object entity;
  private final EntityPersister entityPersister;

  /**
   * (non-Javadoc)
   *
   * @see EntityAction#execute() for more information
   */
  @Override
  public void execute() {
    entityPersister.update(entity);
  }

  /**
   * (non-Javadoc)
   *
   * @see EntityAction#getPriority() for more information
   */
  @Override
  public ActionPriorityEnum getPriority() {
    return ActionPriorityEnum.UPDATE_PRIORITY;
  }
}

