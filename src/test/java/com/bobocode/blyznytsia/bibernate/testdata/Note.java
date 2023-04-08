package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.ManyToOne;
import com.bobocode.blyznytsia.bibernate.annotation.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "notes")
public class Note {

  @Id
  private Long id;

  private String body;

  @ManyToOne(joinColumnName = "person_id")
  private PersonWithOneToMany person;

}
