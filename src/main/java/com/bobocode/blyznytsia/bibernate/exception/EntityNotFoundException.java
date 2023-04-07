package com.bobocode.blyznytsia.bibernate.exception;

public class EntityNotFoundException extends PersistenceException {

	public EntityNotFoundException(Class<?> entityClass, Object primaryKeyValue) {
		super("Entity of type %s with primary key=%s is not found".formatted(entityClass, primaryKeyValue));
	}

	public EntityNotFoundException(Class<?> entityClass, String key, Object keyValue) {
		super("Entity of type %s with %s='%s' is not found".formatted(entityClass, key, keyValue));
	}

}
