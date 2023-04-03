package com.bobocode.blyznytsia.bibernate;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.ManyToOne;
import com.bobocode.blyznytsia.bibernate.annotation.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "notes")
@Setter
@Getter
@ToString(exclude = "user")
public class Note {

  @Id
  private Integer id;

  private String body;

  @ManyToOne(joinColumnName = "user_id")
  private User user;

}
