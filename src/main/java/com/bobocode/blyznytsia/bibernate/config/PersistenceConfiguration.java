package com.bobocode.blyznytsia.bibernate.config;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.List;

/**
 * Record holds and provides access for basic persistence configuration parameters
 * for one or more persistence units.
 */
public record PersistenceConfiguration(List<PersistenceUnitConfiguration> persistenceUnits) {

  private static final int DEFAULT_POOL_SIZE = 10;
  private static final String MISSED_PROPERTY_ERROR_MESSAGE = "Missed required property '%s' in configuration file";

  public PersistenceConfiguration(List<PersistenceUnitConfiguration> persistenceUnits) {
    this.persistenceUnits = isNull(persistenceUnits) ? List.of() : persistenceUnits;
  }

  /**
   * Record holds and provides access for parameters configuration of one persistence unit.
   */
  public record PersistenceUnitConfiguration(String name, DataSource dataSource) {

    public PersistenceUnitConfiguration(String name, DataSource dataSource) {
      this.name = requireNonNull(name, format(MISSED_PROPERTY_ERROR_MESSAGE, "name"));
      this.dataSource =
          requireNonNull(dataSource, format(MISSED_PROPERTY_ERROR_MESSAGE, "dataSource"));
    }

    /**
     * Record holds and provides access for basic parameters that are mandatory for data source configuration.
     */
    public record DataSource(String jdbcUrl, String userName, String password, Integer poolSize,
                             String driverClassName) {
      public DataSource(String jdbcUrl, String userName, String password, Integer poolSize,
                        String driverClassName) {
        this.jdbcUrl =
            requireNonNull(jdbcUrl, format(MISSED_PROPERTY_ERROR_MESSAGE, "jdbcUrl"));
        this.userName =
            requireNonNull(userName, format(MISSED_PROPERTY_ERROR_MESSAGE, "userName"));
        this.password =
            requireNonNull(password, format(MISSED_PROPERTY_ERROR_MESSAGE, "password"));
        this.poolSize = nonNull(poolSize) ? poolSize : DEFAULT_POOL_SIZE;
        this.driverClassName = requireNonNull(driverClassName,
            format(MISSED_PROPERTY_ERROR_MESSAGE, "driverClassName"));
      }
    }
  }

}
