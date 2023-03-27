package com.bobocode.blyznytsia.bibernate.config;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * Record holds and provides access for basic persistence configuration parameters
 * for one or more persistence units.
 */
public record PersistenceConfiguration(List<PersistenceUnitConfiguration> persistenceUnits) {

  public PersistenceConfiguration(List<PersistenceUnitConfiguration> persistenceUnits) {
    this.persistenceUnits = isNull(persistenceUnits) ? List.of() : persistenceUnits;
  }

  /**
   * Record holds and provides access for parameters configuration of one persistence unit.
   */
  public record PersistenceUnitConfiguration(String name, DataSource dataSource) {

    public PersistenceUnitConfiguration(String name, DataSource dataSource) {
      this.name = requireNonNull(name, "Missed required property 'name' in configuration file");
      this.dataSource =
          requireNonNull(dataSource, "Missed required property 'dataSource' in configuration file");
    }

    /**
     * Record holds and provides access for basic parameters that are mandatory for data source configuration.
     */
    public record DataSource(String jdbcUrl, String userName, String password, Integer poolSize,
                             String driverClassName) {
      public DataSource(String jdbcUrl, String userName, String password, Integer poolSize,
                        String driverClassName) {
        this.jdbcUrl =
            requireNonNull(jdbcUrl, "Missed required property 'jdbcUrl' in configuration file");
        this.userName =
            requireNonNull(userName, "Missed required property 'userName' in configuration file");
        this.password =
            requireNonNull(password, "Missed required property 'password' in configuration file");
        this.poolSize = nonNull(poolSize) ? poolSize : 10;
        this.driverClassName = requireNonNull(driverClassName,
            "Missed required property 'driverClassName' in configuration file");
      }
    }
  }

}
