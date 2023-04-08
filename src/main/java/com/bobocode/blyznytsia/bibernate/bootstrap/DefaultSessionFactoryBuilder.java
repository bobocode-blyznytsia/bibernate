package com.bobocode.blyznytsia.bibernate.bootstrap;

import static com.bobocode.blyznytsia.bibernate.config.PersistenceConfiguration.PersistenceUnitConfiguration;

import com.bobocode.blyznytsia.bibernate.config.reader.YamlPropertyReader;
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
public class DefaultSessionFactoryBuilder implements SessionFactoryBuilder {

  private final DataSource dataSource;
  private final PersistenceUnitConfiguration configurations;

  public DefaultSessionFactoryBuilder(String persistenceUnit) {
    this.configurations = getPersistenceConfiguration(persistenceUnit, null);
    this.dataSource = getHikariDataSource();
  }

  public DefaultSessionFactoryBuilder(String persistenceUnit, String propertyFileName) {
    this.configurations = getPersistenceConfiguration(persistenceUnit, propertyFileName);
    this.dataSource = getHikariDataSource();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SessionFactory createSessionFactory() {
    return new SessionFactoryImpl(dataSource);
  }


  private PersistenceUnitConfiguration getPersistenceConfiguration(String persistenceUnit,
                                                                   String propertyFile) {
    var reader = new YamlPropertyReader();
    return Optional.ofNullable(propertyFile)
        .map(file -> reader.readPropertiesFile(file, persistenceUnit))
        .orElseGet(() -> reader.readPropertiesFile(persistenceUnit));
  }

  private DataSource getHikariDataSource() {
    final HikariConfig hikariConfig = new HikariConfig();
    var dataSourceConfiguration = configurations.dataSource();
    hikariConfig.setJdbcUrl(dataSourceConfiguration.jdbcUrl());
    hikariConfig.setUsername(dataSourceConfiguration.userName());
    hikariConfig.setPassword(dataSourceConfiguration.password());
    hikariConfig.setMaximumPoolSize(dataSourceConfiguration.poolSize());
    hikariConfig.setDriverClassName(dataSourceConfiguration.driverClassName());
    return new HikariDataSource(hikariConfig);
  }
}
