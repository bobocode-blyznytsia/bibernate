package com.bobocode.blyznytsia.bibernate.transaction;

public interface Transaction {
  void begin();

  void commit();

  void rollback();

  boolean isActive();

}
