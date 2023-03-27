package com.bobocode.blyznytsia.bibernate.config.reader;

import com.bobocode.blyznytsia.bibernate.config.PersistenceConfiguration;
import static com.bobocode.blyznytsia.bibernate.config.PersistenceConfiguration.PersistenceUnitConfiguration;

/**
 * Reads a configuration file and creates a {@link PersistenceConfiguration},
 * If no file name was provided, the default file named <b>persistence.yml</b> will be looked up
 */
public interface PropertyReader {

  /**
   * Reads configuration from the <b>persistence.yml</b> file. File should be inside resources
   *
   * @param persistenceUnit - persistence unit name, specified in the <code>persistenceUnit.name</code> property
   * @return {@link PersistenceConfiguration}
   */
  PersistenceUnitConfiguration readPropertiesFile(String persistenceUnit);

  /**
   * Read configuration from the file. File should be inside resources
   *
   * @param propertyFile    - resource name
   * @param persistenceUnit - persistence unit name, specified in the <code>persistenceUnit.name</code> property
   * @return {@link PersistenceConfiguration}
   */
  PersistenceUnitConfiguration readPropertiesFile(String propertyFile, String persistenceUnit);


}
