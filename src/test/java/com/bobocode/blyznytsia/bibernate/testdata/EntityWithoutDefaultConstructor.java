package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class EntityWithoutDefaultConstructor {

  @Id
  private Integer id;

  private String someValue;

}
