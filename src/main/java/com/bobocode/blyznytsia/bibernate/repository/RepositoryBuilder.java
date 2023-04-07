package com.bobocode.blyznytsia.bibernate.repository;

import com.bobocode.blyznytsia.bibernate.session.SessionFactory;
import java.lang.reflect.Proxy;

/**
 * Class for {@see BibernateReadonlyRepository} repositories generation
 */
public class RepositoryBuilder {
  private final SessionFactory sessionFactory;

  public RepositoryBuilder(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * Generate dynamic proxy implementation for repository R handling entity E
   *
   * @param repository - interface extending  {@link BibernateReadonlyRepository}
   * @param entityType - the type of entity to be operated within the generated repository
   * @return an instance of repository R implementing all the methods following convention
   *     described in {@link BibernateReadonlyRepository}
   */
  @SuppressWarnings("unchecked")
  public <E, R extends BibernateReadonlyRepository<E, ?>> R buildRepository(Class<R> repository, Class<E> entityType) {
    return (R) Proxy.newProxyInstance(
        repository.getClassLoader(),
        new Class[] {repository},
        new RepositoryProxyInvocationHandler<>(entityType, sessionFactory)
    );
  }

}
