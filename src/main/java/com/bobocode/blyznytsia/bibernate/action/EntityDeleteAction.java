package com.bobocode.blyznytsia.bibernate.action;

import static com.bobocode.blyznytsia.bibernate.action.ActionPriority.DELETE_PRIORITY;

import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityDeleteAction implements EntityAction {
  private final Object entity;
  private final EntityPersister entityPersister;

  /**
   * (non-Javadoc)
   *
   * @see EntityAction#execute() for more information
   */
  @Override
  public void execute() {
    entityPersister.delete(entity);
  }

  /**
   * (non-Javadoc)
   *
   * @see EntityAction#getPriority() for more information
   */
  @Override
  public ActionPriority getPriority() {
    return DELETE_PRIORITY;
  }
}

