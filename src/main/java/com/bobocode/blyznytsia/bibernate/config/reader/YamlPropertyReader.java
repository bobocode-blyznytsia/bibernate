package com.bobocode.blyznytsia.bibernate.config.reader;

import static com.bobocode.blyznytsia.bibernate.config.PersistenceConfiguration.PersistenceUnitConfiguration;
import static java.lang.String.format;

import com.bobocode.blyznytsia.bibernate.config.PersistenceConfiguration;
import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceFileNotFoundException;
import com.bobocode.blyznytsia.bibernate.exception.PersistencePropertyUnrecognizedException;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceUnitNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * implementation of {@link PropertyReader} for YAML file.
 * File should be inside resources' directory.
 *
 * <p>Example:
 * <pre>
 * persistenceUnits:
 *  - name: postgres
 *    dataSource:
 *      jdbcUrl: jdbc:postgresql://localhost:5432/postgres
 *      username: root
 *      password: password
 *      driverClassName: org.postgresql.Driver
 *  - name: h2
 *    dataSource:
 *      jdbcUrl: jdbc:h2:mem:testdb
 *      user: sa
 *      password: password
 *      driverClassName: org.h2.Driver
 * </pre>
 * </p>
 */
@Slf4j
public class YamlPropertyReader implements PropertyReader {

  private static final String DEFAULT_PERSISTENCE_FILE = "persistence.yml";

  /**
   * {@inheritDoc}
   */
  @Override
  public PersistenceUnitConfiguration readPropertiesFile(String persistenceUnit) {
    return readPropertiesFile(DEFAULT_PERSISTENCE_FILE, persistenceUnit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersistenceConfiguration.PersistenceUnitConfiguration readPropertiesFile(
      String propertyFile, String persistenceUnit) {
    ClassLoader contextClassLoader = this.getClass().getClassLoader();

    log.debug("Looking up for the file: {}", propertyFile);
    var propertiesFile = getPropertyFile(propertyFile, contextClassLoader);

    var mapper = new ObjectMapper(new YAMLFactory());
    PersistenceConfiguration configuration;
    try {
      configuration = mapper.readValue(propertiesFile, PersistenceConfiguration.class);
      log.debug("resolved persistence properties from {} file", propertiesFile.getName());
    } catch (UnrecognizedPropertyException ex) {
      throw new PersistencePropertyUnrecognizedException(ex.getMessage());
    } catch (IOException e) {
      throw new BibernateException(e.getMessage());
    }

    return getPersistenceUnitConfiguration(configuration, persistenceUnit);
  }

  private File getPropertyFile(String resourceName, ClassLoader contextClassLoader) {
    var propertyResource = contextClassLoader.getResource(resourceName);
    if (propertyResource == null) {
      String errorMessage = format("Persistence file %s was not found", resourceName);
      log.error(errorMessage);
      throw new PersistenceFileNotFoundException(errorMessage);
    }
    log.info("Found persistence configuration file {}", resourceName);

    return new File(propertyResource.getPath());
  }

  private PersistenceUnitConfiguration getPersistenceUnitConfiguration(
      PersistenceConfiguration configuration,
      String persistenceUnit) {
    log.trace("Looking up for the persistence unit: {}", persistenceUnit);
    var persistenceUnitConfiguration =
        configuration.persistenceUnits()
            .stream()
            .filter(unit -> persistenceUnit.equals(unit.name()))
            .findAny()
            .orElseThrow(() -> new PersistenceUnitNotFoundException(
                format("Persistence unit %s was not found", persistenceUnit)));
    log.debug("Found and resolved configuration for persistence unit {} ", persistenceUnit);
    return persistenceUnitConfiguration;
  }
}
