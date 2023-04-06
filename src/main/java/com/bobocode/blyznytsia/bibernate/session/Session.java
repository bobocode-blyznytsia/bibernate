package com.bobocode.blyznytsia.bibernate.session;

import com.bobocode.blyznytsia.bibernate.query.Query;
import com.bobocode.blyznytsia.bibernate.transaction.Transaction;

public interface Session {
  void persist(Object entity);

  void remove(Object entity);

  <T> T find(Class<T> entityClass, Object primaryKey);

  <T> T findOneBy(Class<T> entityClass, String key, Object value);

  void flush();

  void close();

  boolean isOpen();

  Transaction getTransaction();

  <T> Query<T> createNativeQuery(String sql, Class<T> entityType);

}
