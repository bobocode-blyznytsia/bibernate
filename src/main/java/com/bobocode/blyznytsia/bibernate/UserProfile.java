package com.bobocode.blyznytsia.bibernate;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.OneToOne;
import com.bobocode.blyznytsia.bibernate.annotation.Table;
import lombok.Getter;
import lombok.ToString;

@Table(name = "user_profiles")
@Entity
@ToString(exclude = "user")
@Getter
public class UserProfile {

  @Id
  private Integer id;

  private String title;

  @OneToOne(joinColumnName = "user_id")
  private User user;


}
