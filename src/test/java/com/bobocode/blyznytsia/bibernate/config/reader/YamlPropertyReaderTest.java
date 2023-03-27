package com.bobocode.blyznytsia.bibernate.config.reader;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.blyznytsia.bibernate.exception.PersistenceFileNotFoundException;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceUnitNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class YamlPropertyReaderTest {

  private final YamlPropertyReader reader = new YamlPropertyReader();

  @Test
  @DisplayName("Read properties default file")
  void testReadProperties() {

    var persistenceUnitProperties = reader.readPropertiesFile("h2");
    var dataSourceConfig = persistenceUnitProperties.dataSource();
    Assertions.assertAll(
        () -> assertEquals(persistenceUnitProperties.name(), "h2"),
        () -> assertEquals(dataSourceConfig.jdbcUrl(), "jdbc:h2:mem:testdb"),
        () -> assertEquals(dataSourceConfig.userName(), "sa"),
        () -> assertEquals(dataSourceConfig.password(), "password"),
        () -> assertEquals(dataSourceConfig.poolSize(), 10),
        () -> assertEquals(dataSourceConfig.driverClassName(), "org.h2.Driver")
    );
  }

  @Test
  @DisplayName("Read properties default file throw PersistenceUnitNotFoundException when unit not found")
  void testReadPropertiesPersistenceUnitNotFound() {
    String invalidPersistenceUnitName = "    ";

    assertThatThrownBy(() -> reader.readPropertiesFile(invalidPersistenceUnitName))
        .isInstanceOf(PersistenceUnitNotFoundException.class)
        .hasMessage(String.format("Persistence unit %s was not found", invalidPersistenceUnitName));
  }

  @Test
  @DisplayName("Read properties provided file")
  void testReadPropertiesForFile() {
    var properties = reader.readPropertiesFile(
        "provided-persistence.yml", "postgres");

    assertEquals(properties.name(), "postgres");
  }

  @Test
  @DisplayName("Read properties provided file throw PersistenceUnitNotFoundException when unit not found")
  void testReadPropertiesForFilePersistenceUnitNotFound() {
    String invalidPersistenceUnitName = "    ";

    assertThatThrownBy(() -> reader.readPropertiesFile("provided-persistence.yml",
        invalidPersistenceUnitName))
        .isInstanceOf(PersistenceUnitNotFoundException.class)
        .hasMessage(String.format("Persistence unit %s was not found", invalidPersistenceUnitName));
  }

  @Test
  @DisplayName("Read properties provide file throw PersistenceFileNotFoundException when file not found")
  void testReadPropertiesForFileThrowPersistenceFileNotFoundException() {
    String invalidPersistenceUnitName = "    ";
    String invalidFileName = "invalid-file.yml";

    assertThatThrownBy(
        () -> reader.readPropertiesFile(invalidFileName, invalidPersistenceUnitName))
        .isInstanceOf(PersistenceFileNotFoundException.class)
        .hasMessage(String.format("Persistence file %s was not found", invalidFileName));
  }
}
