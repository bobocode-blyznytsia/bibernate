package com.bobocode.blyznytsia.bibernate.action;

import static com.bobocode.blyznytsia.bibernate.action.ActionPriority.UPDATE_PRIORITY;

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
  public ActionPriority getPriority() {
    return UPDATE_PRIORITY;
  }
}

