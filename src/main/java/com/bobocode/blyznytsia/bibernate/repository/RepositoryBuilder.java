package com.bobocode.blyznytsia.bibernate.repository;

import com.bobocode.blyznytsia.bibernate.session.SessionFactory;
import java.lang.reflect.Proxy;

public class RepositoryBuilder {
  SessionFactory sessionFactory;

  public RepositoryBuilder(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @SuppressWarnings("unchecked")
  public <E, R extends BibernateReadonlyRepository<E, ?>> R buildRepository(Class<R> repository, Class<E> entityType) {
    return (R) Proxy.newProxyInstance(
        repository.getClassLoader(),
        new Class[] {repository},
        new RepositoryProxyInvocationHandler<>(entityType, sessionFactory)
    );
  }

}
