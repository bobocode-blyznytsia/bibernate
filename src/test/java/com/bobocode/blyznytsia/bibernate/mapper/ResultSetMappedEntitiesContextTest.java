package com.bobocode.blyznytsia.bibernate.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bobocode.blyznytsia.bibernate.testdata.PlainPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResultSetMappedEntitiesContextTest {

  private ResultSetMappedEntitiesContext context;

  @BeforeEach
  void setUp() {
    context = new ResultSetMappedEntitiesContext();
  }

  @Test
  void putEntityInContextAndGetEntityFromContext() {
    Class<?> entityType = PlainPerson.class;
    Integer idValue = 1;
    String entityValue = "Joshua Bloch";
    context.putEntityInContext(entityType, idValue, entityValue);
    Object retrievedEntity = context.getEntityFromContext(entityType, idValue);
    assertEquals(entityValue, retrievedEntity);
  }

  @Test
  void putEntityInContextWhenEntityTypeIsPresentInContext() {
    Class<?> entityType = PlainPerson.class;
    Integer idValue1 = 1;
    String entityValue1 = "Joshua Bloch";
    Integer idValue2 = 2;
    String entityValue2 = "Martin Fowler";
    context.putEntityInContext(entityType, idValue1, entityValue1);
    context.putEntityInContext(entityType, idValue2, entityValue2);
    assertTrue(context.isEntityPresentInContext(entityType, idValue1));
    assertTrue(context.isEntityPresentInContext(entityType, idValue2));
  }

  @Test
  void isEntityPresentInContextWhenEntityPresent() {
    Class<?> entityType = PlainPerson.class;
    Integer idValue = 1;
    String entityValue = "Joshua Bloch";
    context.putEntityInContext(entityType, idValue, entityValue);
    boolean isPresent = context.isEntityPresentInContext(entityType, idValue);
    assertTrue(isPresent);
  }

  @Test
  void isEntityPresentInContextWhenEntityNotPresent() {
    Class<?> entityType = PlainPerson.class;
    Integer idValue = 1;
    boolean isPresent = context.isEntityPresentInContext(entityType, idValue);
    assertFalse(isPresent);
  }

}
