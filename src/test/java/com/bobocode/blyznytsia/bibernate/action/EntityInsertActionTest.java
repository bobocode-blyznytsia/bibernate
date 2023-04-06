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
class EntityInsertActionTest {

  @Mock
  private EntityPersister entityPersister;
  @Mock
  private Object entity;

  @InjectMocks
  public EntityInsertAction entityInsertAction;

  @BeforeEach
  public void setUp() {
    entityInsertAction = new EntityInsertAction(entity, entityPersister);
  }

  @Test
  void executeTest() {
    entityInsertAction.execute();

    verify(entityPersister).insert(entity);
  }

  @Test
  void getActionPriorityTest() {
    ActionPriorityEnum actionPriorityEnum = entityInsertAction.getPriority();

    assertEquals(ActionPriorityEnum.INSERT_PRIORITY, actionPriorityEnum);
  }
}
