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

  /**
   * Creates a {@link com.bobocode.blyznytsia.bibernate.query.TypedQuery} instance from given SQL query for type T
   *
   * @param sql - text of the SQL query
   * @param entityType - type of the entity
   * @return new instance of TypedQuery with specified SQL query text and entity type
   */
  <T> Query<T> createNativeQuery(String sql, Class<T> entityType);

}
