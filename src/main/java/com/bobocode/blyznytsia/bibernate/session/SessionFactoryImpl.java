package com.bobocode.blyznytsia.bibernate.session;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SessionFactoryImpl implements SessionFactory {

  private final DataSource dataSource;

  @Override
  public Session createSession() {
    return null;
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public void close() {
    // to  be implemented
  }
}
