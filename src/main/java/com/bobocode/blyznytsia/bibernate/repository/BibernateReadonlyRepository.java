package com.bobocode.blyznytsia.bibernate.repository;

import java.util.Optional;

public interface BibernateReadonlyRepository<E, ID> {
  Optional<E> findById(ID id);

}
