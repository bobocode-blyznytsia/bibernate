package com.bobocode.blyznytsia.bibernate.testdata.entity;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleEntity {
  @Id
  private Long id;
  private String someValue;
}
