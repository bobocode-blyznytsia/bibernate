package com.bobocode.blyznytsia.bibernate.reader;

/**
 * Class that holds property names to read properties from resource file.
 */
public enum PropertyValue {
  PERSISTENCE_UNIT_NAME("persistenceUnit.name"),
  DATA_SOURCE_JDBC_URL("persistenceUnit.dataSource.jdbcUrl"),
  DATA_SOURCE_USER("persistenceUnit.dataSource.user"),
  DATA_SOURCE_PASSWORD("persistenceUnit.dataSource.password"),
  DATA_SOURCE_DRIVER("persistenceUnit.dataSource.driverClassName");

  public final String value;

  PropertyValue(String propertyName) {
    this.value = propertyName;
  }
}
