package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;

@Entity
public class Person {
  @Id
  private String name;
  private String lastName;
  private Long age;
  private String status;
}

