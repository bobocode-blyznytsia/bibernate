package com.bobocode.blyznytsia.bibernate.context;

import static org.assertj.core.api.Assertions.assertThat;

import com.bobocode.blyznytsia.bibernate.model.EntityKey;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersistenceContextImplTest {
  private PersistenceContext persistenceContext;
  private EntityKey entityKey;
  private SampleEntity sampleEntity;

  @BeforeEach
  void setUp() {
    persistenceContext = new PersistenceContextImpl();
    entityKey = new EntityKey<>(SampleEntity.class, 1);
    sampleEntity = new SampleEntity(1L, "Entity A");
  }

  @Test
  void addEntityToCacheAndRetrieveIt() {
    persistenceContext.addEntityToCache(entityKey, sampleEntity);

    Object cachedEntity = persistenceContext.getCachedEntity(entityKey);
    assertThat(cachedEntity).isExactlyInstanceOf(SampleEntity.class);
    assertThat((SampleEntity) cachedEntity).usingRecursiveComparison().isEqualTo(sampleEntity);
  }

  @Test
  void dirtyCheckForUnmodifiedEntity() {
    persistenceContext.addEntityToCache(entityKey, sampleEntity);

    Map<EntityKey, Object> dirtyEntities = persistenceContext.dirtyCheck();
    assertThat(dirtyEntities).isEmpty();
  }

  @Test
  void dirtyCheckForModifiedEntity() {
    persistenceContext.addEntityToCache(entityKey, sampleEntity);
    sampleEntity.setSomeValue("Entity B");

    Map<EntityKey, Object> dirtyEntities = persistenceContext.dirtyCheck();
    assertThat(dirtyEntities).containsOnlyKeys(entityKey);
    assertThat((SampleEntity) dirtyEntities.get(entityKey)).usingRecursiveComparison()
        .isEqualTo(sampleEntity);
  }
}