package com.bobocode.blyznytsia.bibernate;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.ManyToOne;
import com.bobocode.blyznytsia.bibernate.annotation.OneToOne;
import lombok.ToString;

@Entity
@ToString
public class Note {

  @Id
  private Integer id;

  private String body;

  @OneToOne(joinColumnName = "user_id")
  private User user;

}
