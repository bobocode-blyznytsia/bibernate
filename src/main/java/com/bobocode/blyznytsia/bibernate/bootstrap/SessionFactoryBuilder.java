package com.bobocode.blyznytsia.bibernate.bootstrap;

import com.bobocode.blyznytsia.bibernate.session.SessionFactory;

/**
 * Starting point to create {@link SessionFactory session factory}
 */
public interface SessionFactoryBuilder {

  /**
   * creates a new {@link SessionFactory}
   *
   * @return {@link SessionFactory}
   */
  SessionFactory createSessionFactory();
}
