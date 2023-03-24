package com.bobocode.blyznytsia.bibernate.testdata.entity;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;

@Entity
public class EntityWithoutId {
  // @Id annotation is missing on purpose
  private Long id;
}
