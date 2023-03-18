package com.bobocode.blyznytsia.bibernate.session;

import com.bobocode.blyznytsia.bibernate.transaction.Transaction;

public interface Session {
  void persist(Object entity);

  void remove(Object entity);

  <T> T find(Class<T> entityClass, Object primaryKey);

  void flush();

  void close();

  boolean isOpen();

  Transaction getTransaction();

}
