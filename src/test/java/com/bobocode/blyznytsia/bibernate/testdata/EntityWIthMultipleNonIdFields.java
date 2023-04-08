package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import lombok.Data;

@Entity
@Data
public class EntityWIthMultipleNonIdFields {
  @Id
  private Long id;
  private String firstField;
  private int secondField;
  private long ThirdField;
  private boolean fourthField;

}
