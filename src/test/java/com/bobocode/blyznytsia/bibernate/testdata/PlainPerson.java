package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.Table;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Entity
@Table(name = "persons")
public class PlainPerson {

  @Id
  private Integer id;

  private String firstName;

  private String lastName;

  private LocalDate birthday;

}
