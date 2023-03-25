package com.bobocode.blyznytsia.bibernate.bootstrap;

import com.bobocode.blyznytsia.bibernate.config.PersistenceConfiguration;
import com.bobocode.blyznytsia.bibernate.reader.PropertyReader;
import com.bobocode.blyznytsia.bibernate.session.SessionFactory;
import com.bobocode.blyznytsia.bibernate.session.SessionFactoryImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Optional;
import javax.sql.DataSource;

/**
 * Default implementation of {@link SessionFactoryBuilder}. Use {@link HikariConfig} for
 * {@link DataSource} setup.
 */
public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {

  private final DataSource dataSource;
  private final PersistenceConfiguration configuration;

  public SessionFactoryBuilderImpl(PropertyReader reader, String persistenceUnit) {
    this.configuration = getPersistenceConfiguration(reader, persistenceUnit, null);
    this.dataSource = getHikariDataSource();
  }

  public SessionFactoryBuilderImpl(PropertyReader reader, String persistenceUnit,
                                   String propertyFile) {
    this.configuration = getPersistenceConfiguration(reader, persistenceUnit, propertyFile);
    this.dataSource = getHikariDataSource();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SessionFactory createSessionFactory() {
    return new SessionFactoryImpl(dataSource);
  }


  private PersistenceConfiguration getPersistenceConfiguration(PropertyReader reader,
                                                               String persistenceUnit,
                                                               String propertyFile) {
    return Optional.ofNullable(propertyFile)
        .map(file -> reader.readPropertiesFile(file, persistenceUnit))
        .orElseGet(() -> reader.readPropertiesFile(persistenceUnit));
  }

  private DataSource getHikariDataSource() {
    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(configuration.getJdbcUrl());
    hikariConfig.setUsername(configuration.getUsername());
    hikariConfig.setPassword(configuration.getPassword());
    hikariConfig.setMaximumPoolSize(configuration.getPoolSize());
    hikariConfig.setDriverClassName(configuration.getDriverClassName());
    return new HikariDataSource(hikariConfig);
  }
}
