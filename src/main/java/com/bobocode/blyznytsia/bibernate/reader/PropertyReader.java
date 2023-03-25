package com.bobocode.blyznytsia.bibernate.reader;

import com.bobocode.blyznytsia.bibernate.config.PersistenceConfiguration;

/**
 * Reads a .yml file and creates a {@link PersistenceConfiguration},
 * If no file name was provided, the default file named <b>persistence.yml</b> will be looked up <p>
 * <p>
 * Example:
 * <pre>
 *
 * persistenceUnit:
 *   name: postgres
 *   dataSource:
 *     jdbcUrl: jdbc:postgresql://localhost:5432/postgres
 *     user: root
 *     password: password
 *     driverClassName: org.postgresql.Driver
 * </pre>
 * <p>
 */
public interface PropertyReader {

    /**
     * Reads configuration from the <b>persistence.yml</b> file. File should be inside resources
     *
     * @param persistenceUnit - persistence unit name, specified in the <code>persistenceUnit.name</code> property
     * @return {@link PersistenceConfiguration}
     */
    PersistenceConfiguration readPropertiesFile(String persistenceUnit);

    /**
     * Read configuration from the file. File should be inside resources
     *
     * @param propertyFile    - resource name
     * @param persistenceUnit - persistence unit name, specified in the <code>persistenceUnit.name</code> property
     * @return {@link PersistenceConfiguration}
     */
    PersistenceConfiguration readPropertiesFile(String propertyFile, String persistenceUnit);
}
