package com.bobocode.blyznytsia.bibernate.exception;

public class PersistenceUnitNotFoundException extends BibernateException {

    public PersistenceUnitNotFoundException(String message) {
        super(message);
    }
}
