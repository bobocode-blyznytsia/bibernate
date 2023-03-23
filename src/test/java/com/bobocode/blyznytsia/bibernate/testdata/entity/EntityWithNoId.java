package com.bobocode.blyznytsia.bibernate.testdata.entity;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;

@Entity
public class EntityWithNoId {
  // @Id annotation is missing on purpose
  Long id;
}
