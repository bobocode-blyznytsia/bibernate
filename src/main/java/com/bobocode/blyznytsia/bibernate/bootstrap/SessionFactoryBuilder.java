package com.bobocode.blyznytsia.bibernate.bootstrap;

import com.bobocode.blyznytsia.bibernate.session.SessionFactory;

public interface SessionFactoryBuilder {

  SessionFactory createSessionFactory();
}
