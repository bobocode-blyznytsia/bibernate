package com.bobocode.blyznytsia.bibernate;

import com.bobocode.blyznytsia.bibernate.annotation.Column;
import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.OneToMany;
import com.bobocode.blyznytsia.bibernate.annotation.OneToOne;
import com.bobocode.blyznytsia.bibernate.annotation.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Setter
@Getter
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

  @OneToOne(mappedBy = "user")
  private UserProfile profile;

  @OneToMany(mappedBy = "user")
  private List<Note> notes;



}
