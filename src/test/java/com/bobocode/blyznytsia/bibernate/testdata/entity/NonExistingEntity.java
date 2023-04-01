package com.bobocode.blyznytsia.bibernate.testdata.entity;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;

/**
 * This entity does not exist in the DB, therefore, causes SQLException
 */
@Entity
public class NonExistingEntity {
  @Id
  Long id;
}
