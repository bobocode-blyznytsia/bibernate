package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;

@Entity
public class EntityWithMultipleIds {
  @Id
  private Long id1;
  @Id
  private Long id2;
}
