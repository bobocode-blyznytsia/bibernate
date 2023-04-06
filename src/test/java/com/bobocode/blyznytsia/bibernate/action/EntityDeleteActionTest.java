package com.bobocode.blyznytsia.bibernate.action;

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
class EntityDeleteActionTest {
  @Mock
  private EntityPersister entityPersister;
  @Mock
  private Object entity;
  @InjectMocks
  public EntityDeleteAction entityDeleteAction;

  @BeforeEach
  public void setUp() {
    entityDeleteAction = new EntityDeleteAction(entity, entityPersister);
  }

  @Test
  void executeTest() {
    entityDeleteAction.execute();

    verify(entityPersister).delete(entity);
  }

  @Test
  void getActionPriorityTest() {
    ActionPriorityEnum actionPriorityEnum = entityDeleteAction.getPriority();

    assertEquals(ActionPriorityEnum.DELETE_PRIORITY, actionPriorityEnum);
  }
}
