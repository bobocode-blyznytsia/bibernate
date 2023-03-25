package com.bobocode.blyznytsia.bibernate.reader;

import com.bobocode.blyznytsia.bibernate.config.PersistenceConfiguration;

import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceFileNotFoundException;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceUnitNotFoundException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import static com.bobocode.blyznytsia.bibernate.reader.PropertyValue.*;

/**
 * implementation of {@link PropertyReader} for YAML file.
 */
@Slf4j
public class YamlPropertyReader implements PropertyReader {

  private static final String NAME_PROPERTY = "name";
  private static final String DEFAULT_PERSISTENCE_FILE = "persistence.yml";
  private static final String PERSISTENCE_UNIT_PROPERTY = "persistenceUnit";

  /**
   * {@inheritDoc}
   */
  @Override
  public PersistenceConfiguration readPropertiesFile(String persistenceUnit) {
    return readPropertiesFile(DEFAULT_PERSISTENCE_FILE, persistenceUnit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersistenceConfiguration readPropertiesFile(String propertyFile, String persistenceUnit) {
    ClassLoader contextClassLoader = this.getClass().getClassLoader();

    log.info("Looking up for the file: {}", propertyFile);
    InputStream persistencePropertiesFile = getPropertyFile(propertyFile, contextClassLoader);
    var properties = getPropertiesForUnit(persistencePropertiesFile, persistenceUnit);
    return createPersistenceConfiguration(properties);
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> getPropertiesForUnit(InputStream propertyFile, String unitName) {
    Yaml yaml = new Yaml();

    Iterable<Object> objectIterable = yaml.loadAll(propertyFile);

    log.trace("Looking up for the persistence unit: {}", unitName);
    Object propertyBlock = StreamSupport.stream(objectIterable.spliterator(), false)
        .filter(propertiesBlock -> hasCorrectPersistenceUnitName(unitName, propertiesBlock))
        .findAny()
        .orElseThrow(() -> {
          String errorMessage = String.format("Persistence unit %s was not found", unitName);
          log.error(errorMessage);
          return new PersistenceUnitNotFoundException(errorMessage);
        });
    log.trace("Persistence unit {} was found", unitName);

    log.trace("Resolving properties from the property block for the current persistence unit");
    Object collectedProperties = mapObjectTo(propertyBlock, Map.class).entrySet().stream()
        .flatMap(propertyRoot -> getProperties(mapObjectTo(propertyRoot, Map.Entry.class)))
        .collect(Collectors.toMap(property -> mapObjectTo(property, Map.Entry.class).getKey(),
            property -> mapObjectTo(property, Map.Entry.class).getValue()));

    return mapObjectTo(collectedProperties, Map.class);
  }

  private <T> T mapObjectTo(Object object, Class<T> mappingClass) {
    return mappingClass.cast(object);
  }

  private Stream<Map.Entry<String, String>> getProperties(Map.Entry<String, Object> propertyMap) {
    return getPropertyName(propertyMap.getKey(), propertyMap);
  }

  @SuppressWarnings("unchecked")
  private Stream<Map.Entry<String, String>> getPropertyName(
      String propertyNamePrefix, Map.Entry<String, ?> currentProperty) {
    if (Map.class.isAssignableFrom(currentProperty.getValue().getClass())) {
      return mapObjectTo(currentProperty.getValue(), Map.class).entrySet().stream()
          .flatMap(property -> {
            Map.Entry<String, Object> innerProperty = mapObjectTo(property, Map.Entry.class);
            return getPropertyName(
                propertyNamePrefix + "." + innerProperty.getKey(), innerProperty);
          });
    }
    String propertyName =
        propertyNamePrefix.contains(currentProperty.getKey()) ? propertyNamePrefix :
            propertyNamePrefix + ".";
    String propertyValue = mapObjectTo(currentProperty.getValue(), String.class);

    log.trace("Found property: {}={}", propertyName, propertyValue);
    return Stream.of(Map.entry(propertyName, propertyValue));
  }

  private boolean hasCorrectPersistenceUnitName(String persistenceUnitName,
                                                Object propertiesBlock) {
    if (mapObjectTo(propertiesBlock, Map.class).containsKey(PERSISTENCE_UNIT_PROPERTY)) {
      Object persistenceUnit =
          mapObjectTo(propertiesBlock, Map.class).get(PERSISTENCE_UNIT_PROPERTY);
      if (mapObjectTo(persistenceUnit, Map.class).containsKey(NAME_PROPERTY)) {
        Object currentPersistenceUnitName =
            mapObjectTo(persistenceUnit, Map.class).get(NAME_PROPERTY);
        return currentPersistenceUnitName.equals(persistenceUnitName);
      }
    }
    return false;
  }

  private InputStream getPropertyFile(String resourceName, ClassLoader contextClassLoader) {
    InputStream propertyFile = contextClassLoader.getResourceAsStream(resourceName);
    if (propertyFile == null) {
      String errorMessage = String.format("Persistence file %s was not found", resourceName);
      log.error(errorMessage);
      throw new PersistenceFileNotFoundException(errorMessage);
    }
    log.info("Persistence file {} was found", resourceName);

    return propertyFile;
  }

  public static PersistenceConfiguration createPersistenceConfiguration(Map<String, String> properties) {
    return PersistenceConfiguration.builder()
        .unitName(readProperty(properties, PERSISTENCE_UNIT_NAME))
        .jdbcUrl(readProperty(properties, DATA_SOURCE_JDBC_URL))
        .username(readProperty(properties, DATA_SOURCE_USER))
        .password(readProperty(properties, DATA_SOURCE_PASSWORD))
        .driverClassName(readProperty(properties, DATA_SOURCE_DRIVER))
        .build();
  }

  private static String readProperty(Map<String, String> properties, PropertyValue propertyValue) {
    return Optional.of(properties.get(propertyValue.value))
        .orElseThrow(() -> new BibernateException("Persistence name was not provided in properties file"));
  }
}
