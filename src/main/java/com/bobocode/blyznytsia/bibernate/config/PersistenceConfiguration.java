package com.bobocode.blyznytsia.bibernate.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Class holds and provides access for basic parameters that are mandatory for data source configuration.
 */
@Getter
@Setter
@Builder
public class PersistenceConfiguration {

  private String unitName;
  private String jdbcUrl;
  private String username;
  private String password;
  @Builder.Default
  private int poolSize = 10;
  private String driverClassName;
}
