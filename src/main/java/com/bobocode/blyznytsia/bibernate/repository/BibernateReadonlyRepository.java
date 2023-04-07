package com.bobocode.blyznytsia.bibernate.repository;

import java.util.Optional;

public interface BibernateReadonlyRepository<E, K> {
  Optional<E> findById(K id);

}
