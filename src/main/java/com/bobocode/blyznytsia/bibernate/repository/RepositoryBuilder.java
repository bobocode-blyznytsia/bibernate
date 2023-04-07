package com.bobocode.blyznytsia.bibernate.repository;

import com.bobocode.blyznytsia.bibernate.session.SessionFactory;
import java.lang.reflect.Proxy;

/**
 * Class for {@See BibernateReadonlyRepository} repositories generation
 */
public class RepositoryBuilder {
  SessionFactory sessionFactory;

  public RepositoryBuilder(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * Generate dymanic proxy implementation for repository R handling entity E
   *
   * @param repository
   * @param entityType
   * @return an instance of repository R implementing all the methods folowing convention
   *         described in {@Link BibenrateReadonlyRepository}
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
