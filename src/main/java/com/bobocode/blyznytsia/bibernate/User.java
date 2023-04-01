package com.bobocode.blyznytsia.bibernate;

import com.bobocode.blyznytsia.bibernate.annotation.Column;
import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.OneToMany;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@ToString
public class User {

  @Id
  private Integer id;

  @Column(name = "first_name")
  private String first;

  private String lastName;

  private int age;

  private LocalDateTime birthday;

  private LocalDate someDate;

  @OneToMany
  private List<Note> notes;



}
