package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.annotation.OneToMany;
import com.bobocode.blyznytsia.bibernate.annotation.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
@Entity
@Table(name = "persons")
public class PersonWithOneToMany {

  @Id
  private Integer id;

  private String firstName;

  private String lastName;

  private LocalDate birthday;

  @OneToMany(mappedBy = "person")
  private List<Note> notes;

}
