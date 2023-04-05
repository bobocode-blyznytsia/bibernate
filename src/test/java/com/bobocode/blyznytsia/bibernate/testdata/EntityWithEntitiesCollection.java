package com.bobocode.blyznytsia.bibernate.testdata;

import com.bobocode.blyznytsia.bibernate.annotation.Entity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityWithEntitiesCollection {
  private int id;
  private String name;
  private List<SampleEntity> tags;
}
