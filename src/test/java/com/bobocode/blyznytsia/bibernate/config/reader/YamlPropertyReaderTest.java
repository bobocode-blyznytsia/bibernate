package com.bobocode.blyznytsia.bibernate.config.reader;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bobocode.blyznytsia.bibernate.exception.PersistenceFileNotFoundException;
import com.bobocode.blyznytsia.bibernate.exception.PersistencePropertyUnrecognizedException;
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
        () -> assertEquals("h2", persistenceUnitProperties.name()),
        () -> assertEquals("jdbc:h2:mem:testdb", dataSourceConfig.jdbcUrl()),
        () -> assertEquals("sa", dataSourceConfig.userName()),
        () -> assertEquals("password", dataSourceConfig.password()),
        () -> assertEquals(10, dataSourceConfig.poolSize()),
        () -> assertEquals("org.h2.Driver", dataSourceConfig.driverClassName())
    );
  }

  @Test
  @DisplayName("Read properties default file throw PersistenceUnitNotFoundException when unit not found")
  void shouldReadPropertiesPersistenceUnitNotFound() {
    String invalidPersistenceUnitName = "    ";

    assertThatThrownBy(() -> reader.readPropertiesFile(invalidPersistenceUnitName))
        .isInstanceOf(PersistenceUnitNotFoundException.class)
        .hasMessage(String.format("Persistence unit %s was not found", invalidPersistenceUnitName));
  }

  @Test
  @DisplayName("Read properties provided file")
  void shouldReadPropertiesFromFile() {
    var properties = reader.readPropertiesFile(
        "provided-persistence.yml", "postgres");

    assertEquals("postgres", properties.name());
  }

  @Test
  @DisplayName("Read properties provided file throw PersistenceUnitNotFoundException when unit not found")
  void shouldReadPropertiesFromFilePersistenceUnitNotFound() {
    String invalidPersistenceUnitName = "    ";

    assertThatThrownBy(() -> reader.readPropertiesFile("provided-persistence.yml",
        invalidPersistenceUnitName))
        .isInstanceOf(PersistenceUnitNotFoundException.class)
        .hasMessage(String.format("Persistence unit %s was not found", invalidPersistenceUnitName));
  }

  @Test
  @DisplayName("Read properties provide file throw PersistenceFileNotFoundException when file not found")
  void shouldReadPropertiesFroFileThrowPersistenceFileNotFoundException() {
    String invalidPersistenceUnitName = "    ";
    String invalidFileName = "invalid-file.yml";

    assertThatThrownBy(
        () -> reader.readPropertiesFile(invalidFileName, invalidPersistenceUnitName))
        .isInstanceOf(PersistenceFileNotFoundException.class)
        .hasMessage(String.format("Persistence file %s was not found", invalidFileName));
  }

  @Test
  @DisplayName("Read properties provide file throw PersistencePropertyUnrecognizedException when file not found")
  void shouldReadPropertiesFroFileThrowPersistencePropertyUnrecognizedException() {
    String invalidPersistenceUnitName = "invalid-property";
    String invalidFileName = "invalid-persistence.yml";

    assertThatThrownBy(
        () -> reader.readPropertiesFile(invalidFileName, invalidPersistenceUnitName))
        .isInstanceOf(PersistencePropertyUnrecognizedException.class);
  }
}
