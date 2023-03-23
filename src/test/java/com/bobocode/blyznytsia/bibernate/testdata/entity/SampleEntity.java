package com.bobocode.blyznytsia.bibernate.testdata.entity;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class SampleEntity {
  @Id
  Long id;
  String someValue;
}
