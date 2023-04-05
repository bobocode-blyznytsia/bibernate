package com.bobocode.blyznytsia.bibernate.testdata.entity;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NestedEntity {
  private int id;
  private String name;
  private SampleEntity relatedEntity;
}
