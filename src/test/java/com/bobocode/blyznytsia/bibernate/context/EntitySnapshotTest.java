package com.bobocode.blyznytsia.bibernate.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.bobocode.blyznytsia.bibernate.testdata.entity.EntityWithCollection;
import com.bobocode.blyznytsia.bibernate.testdata.EntityWithEntitiesCollection;
import com.bobocode.blyznytsia.bibernate.testdata.NestedEntity;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EntitySnapshotTest {

  @Test
  void testIsDirtyDifferentObjectClasses() {
    SampleEntity entityA = new SampleEntity(1L, "Entity A");
    NestedEntity entityD = new NestedEntity(1, "Entity D", entityA);
    EntitySnapshot snapshot = new EntitySnapshot(entityA);
    assertThatThrownBy(() -> snapshot.isDirty(entityD)).isInstanceOf(IllegalArgumentException.class)
        .hasMessageMatching("Entity and originalEntityState must be of the same class");
  }

  @ParameterizedTest
  @MethodSource("testCases")
  void testIsDirty(Object entity, Object modifiedEntity, boolean result) {
    EntitySnapshot snapshot = new EntitySnapshot(entity);
    assertThat(snapshot.isDirty(modifiedEntity)).isEqualTo(result);
  }

  private static Stream<Arguments> testCases() {
    SampleEntity sampleEntityAVer1 = new SampleEntity(1L, "Entity A");
    SampleEntity sampleEntityAVer2 = new SampleEntity(1L, "Entity A");
    SampleEntity sampleEntityAVerId = new SampleEntity(2L, "Entity A");
    SampleEntity sampleEntityB = new SampleEntity(1L, "Entity B");
    EntityWithCollection entityWithCollectionCVer1 =
        new EntityWithCollection(1, "Entity C", Arrays.asList("tag1", "tag2"));
    EntityWithCollection entityWithCollectionCVer2 =
        new EntityWithCollection(1, "Entity C", Arrays.asList("tag1", "tag2"));
    EntityWithCollection entityWithCollectionCDiffSize =
        new EntityWithCollection(1, "Entity C", Arrays.asList("tag1", "tag2", "tag3"));
    NestedEntity nestedEntityDAVer1 =
        new NestedEntity(1, "Entity D", sampleEntityAVer1);
    NestedEntity nestedEntityDAVer2 =
        new NestedEntity(1, "Entity D", sampleEntityAVer1);
    NestedEntity nestedEntityDB =
        new NestedEntity(1, "Entity D", sampleEntityB);
    EntityWithEntitiesCollection entitiesCollectionFVer1 =
        new EntityWithEntitiesCollection(1, "Entity F",
            Arrays.asList(sampleEntityAVer1, sampleEntityAVer2));
    EntityWithEntitiesCollection entitiesCollectionFVer2 =
        new EntityWithEntitiesCollection(1, "Entity F",
            Arrays.asList(sampleEntityAVer2, sampleEntityAVer1));
    EntityWithEntitiesCollection entitiesCollectionFVer3 =
        new EntityWithEntitiesCollection(1, "Entity F",
            Arrays.asList(sampleEntityAVer1, sampleEntityAVer1));
    EntityWithEntitiesCollection entitiesCollectionFDiffSize =
        new EntityWithEntitiesCollection(1, "Entity F",
            Arrays.asList(sampleEntityAVer2, sampleEntityAVer1, sampleEntityAVerId));

    return Stream.of(
        Arguments.of(sampleEntityAVer1, sampleEntityAVer2, false),
        Arguments.of(sampleEntityAVer1, sampleEntityB, true),
        Arguments.of(sampleEntityAVer1, sampleEntityAVerId, true),
        Arguments.of(sampleEntityAVer1, null, true),
        Arguments.of(entityWithCollectionCVer1, entityWithCollectionCVer2, false),
        Arguments.of(entityWithCollectionCVer1, entityWithCollectionCDiffSize, true),
        Arguments.of(nestedEntityDAVer1, nestedEntityDAVer2, false),
        Arguments.of(nestedEntityDAVer1, nestedEntityDB, true),
        Arguments.of(entitiesCollectionFVer1, entitiesCollectionFVer2, false),
        Arguments.of(entitiesCollectionFVer1, entitiesCollectionFVer3, false),
        Arguments.of(entitiesCollectionFVer1, entitiesCollectionFDiffSize, true)
    );
  }

}