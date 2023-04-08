package com.bobocode.blyznytsia.bibernate.exception;

/**
 * Exception class for errors that can be thrown when user is trying to use unsupported by Bibernate
 * framework features.
 */
public class NotSupportedException extends BibernateException {

  public NotSupportedException(String message) {
    super(message);
  }

  public static NotSupportedException fieldTypeIsNotSupported(Class<?> fieldType) {
    return new NotSupportedException("Field type %s is not supported by Bibernate framework."
        .formatted(fieldType));
  }

  public static NotSupportedException sqlTypeIsNotSupported(String entityFieldName) {
    return new NotSupportedException(
        String.format("Faced with issue while trying to get value for field %s."
            + "Only java.sql.Timestamp and java.sql.Date is are supported.", entityFieldName));
  }

  public static NotSupportedException relationIsNotSupported(String entityFieldName) {
    return new NotSupportedException(String.format("Only one-to-one, one-to-many, many-to-one "
        + "relations are supported for field %s.", entityFieldName));
  }

  public static NotSupportedException onlyListIsSupportedForOneToManyRelation(String entityFieldName) {
    return new NotSupportedException(String.format("Only list type is supported in @OneToMany "
        + "relation, please change type to java.util.List in field %s.", entityFieldName));
  }

}
