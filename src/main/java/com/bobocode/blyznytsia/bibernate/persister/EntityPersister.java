package com.bobocode.blyznytsia.bibernate.persister;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public interface EntityPersister {
  <T> Optional<T> findById(Class<T> entityType, Object id);

  <T> Optional<T> findOneBy(Class<T> entityType, Field key, Object value);

  <T> List<T> findAll(Class<T> entityType, Field key, Object value);

  <T> T insert(T entity);

  <T> T update(T entity);

  <T> T delete(T entity);
}
