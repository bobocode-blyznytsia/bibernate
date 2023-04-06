package com.bobocode.blyznytsia.bibernate.bootstrap;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultSessionFactoryBuilderTest {

  @Test
  @DisplayName("Create session factory with datasource from default file")
  void shouldCreateSessionFactory() {
    var sessionFactory = new DefaultSessionFactoryBuilder("h2").createSessionFactory();

    assertNotNull(sessionFactory);
  }
}
