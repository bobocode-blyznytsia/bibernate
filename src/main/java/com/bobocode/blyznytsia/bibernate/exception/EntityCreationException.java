package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Exception class for errors that occur while trying to create entity instance
 */
public class EntityCreationException extends BibernateException {

  public EntityCreationException(String message, Throwable cause) {
    super(message, cause);
  }

  public EntityCreationException(Class<?> entityType) {
    super("Failed to create instance of entity. Default constructor is required in class %s."
        .formatted(entityType.getName()));
  }
}
