package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Column;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.Table;

@Table(name = "custom_entity_table_name")
public class AnnotatedSampleEntity {
  @Id
  private Long id;

  @Column(name = "custom_column_name")
  private String someValue;
}
