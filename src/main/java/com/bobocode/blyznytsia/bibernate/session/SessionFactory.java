package com.bobocode.blyznytsia.bibernate.session;

public interface SessionFactory {
  Session createSession();
  boolean isOpen();
  void close();
}
