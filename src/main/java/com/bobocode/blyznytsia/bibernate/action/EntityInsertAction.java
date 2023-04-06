package com.bobocode.blyznytsia.bibernate.action;

import static com.bobocode.blyznytsia.bibernate.action.ActionPriority.INSERT_PRIORITY;

import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityInsertAction implements EntityAction {
  private final Object entity;
  private final EntityPersister entityPersister;

  /**
   * (non-Javadoc)
   *
   * @see EntityAction#execute() for more information
   */
  @Override
  public void execute() {
    entityPersister.insert(entity);
  }

  /**
   * (non-Javadoc)
   *
   * @see EntityAction#getPriority() for more information
   */
  @Override
  public ActionPriority getPriority() {
    return INSERT_PRIORITY;
  }
}

