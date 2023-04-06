package com.bobocode.blyznytsia.bibernate.action;

import static com.bobocode.blyznytsia.bibernate.action.ActionPriority.UPDATE_PRIORITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntityUpdateActionTest {

  @Mock
  private EntityPersister entityPersister;
  @Mock
  private Object entity;
  @InjectMocks
  public EntityUpdateAction entityUpdateAction;

  @BeforeEach
  public void setUp() {
    entityUpdateAction = new EntityUpdateAction(entity, entityPersister);
  }

  @Test
  void executeTest() {
    entityUpdateAction.execute();

    verify(entityPersister).update(entity);
  }

  @Test
  void getActionPriorityTest() {
    ActionPriority actionPriority = entityUpdateAction.getPriority();

    assertEquals(UPDATE_PRIORITY, actionPriority);
  }
}
