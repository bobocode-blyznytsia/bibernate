package com.bobocode.blyznytsia.bibernate.mapper;

import static com.bobocode.blyznytsia.bibernate.util.RelationsUtil.getMappedByFieldInChildClass;
import static com.bobocode.blyznytsia.bibernate.util.RelationsUtil.verifyJoinColumnNameIsNotEmpty;
import static com.bobocode.blyznytsia.bibernate.util.RelationsUtil.verifyMappedByIsNotEmpty;
import static com.bobocode.blyznytsia.bibernate.util.ResultSetUtil.getEntityIdValueFromResultSet;
import static com.bobocode.blyznytsia.bibernate.util.ResultSetUtil.getValueFromResultSet;

import com.bobocode.blyznytsia.bibernate.annotation.ManyToOne;
import com.bobocode.blyznytsia.bibernate.annotation.OneToMany;
import com.bobocode.blyznytsia.bibernate.annotation.OneToOne;
import com.bobocode.blyznytsia.bibernate.exception.MalformedEntityException;
import com.bobocode.blyznytsia.bibernate.exception.NotSupportedException;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class that provides functionality resolving entity relation fields which marked with annotations:
 * {@link OneToOne}, {@link ManyToOne}, {@link OneToMany}.
 */
@Slf4j
@RequiredArgsConstructor
public class RelationResolver {

  private final EntityPersister entityPersister;
  private final ResultSetMappedEntitiesContext mappedEntitiesContext = new ResultSetMappedEntitiesContext();

  /**
   * Returns a foreign entity instance, that can be set in particular entity field.
   *
   * @param entityField the entity foreign field, it can be reference field on other entity
   * or {@link List} that has as generic type other entity
   * @param rs instance of {@link ResultSet}
   * @param entity current entity instance
   * @return foreign entity instance
   */
  public Object resolveRelationField(Field entityField, ResultSet rs, Object entity) {
    if (entityField.isAnnotationPresent(ManyToOne.class)) {
      return resolveManyToOneRelationField(entityField, rs);
    } else if (entityField.isAnnotationPresent(OneToOne.class)) {
      return resolveOneToOneRelationField(entityField, rs, entity);
    } else if (entityField.isAnnotationPresent(OneToMany.class)) {
      return resolveOneToManyRelationField(entityField, rs, entity);
    } else {
      throw NotSupportedException.relationIsNotSupported(entityField.getName());
    }
  }

  private Object resolveManyToOneRelationField(Field entityField, ResultSet rs) {
    String joinColumnName = entityField.getAnnotation(ManyToOne.class).joinColumnName();
    return resolveSingleRelationalFieldOnChildSide(joinColumnName, entityField, rs);
  }

  private <T> Object resolveOneToOneRelationField(Field entityField, ResultSet rs, T entity) {
    String joinColumnName = entityField.getAnnotation(OneToOne.class).joinColumnName();
    String mappedBy = entityField.getAnnotation(OneToOne.class).mappedBy();
    if (!joinColumnName.isEmpty() && mappedBy.isEmpty()) {
      return resolveSingleRelationalFieldOnChildSide(joinColumnName, entityField, rs);
    } else if (joinColumnName.isEmpty() && !mappedBy.isEmpty()) {
      return resolveOneToOneRelationFieldOnParentSide(entityField, rs, entity, mappedBy);
    } else {
      throw new MalformedEntityException("Either join column name or mapped by must be specified.");
    }
  }

  private <T> Object resolveOneToOneRelationFieldOnParentSide(Field entityField, ResultSet rs, T entity,
      String mappedBy) {
    Class<?> entityType = entity.getClass();
    Object idValue = getEntityIdValueFromResultSet(rs, entityType);
    mappedEntitiesContext.putEntityInContext(entityType, idValue, entity);
    Class<?> fieldEntityClass = entityField.getType();
    Field mappedByField = getMappedByFieldInChildClass(fieldEntityClass, mappedBy);
    String childJoinColumnName = mappedByField.getAnnotation(OneToOne.class).joinColumnName();

    Supplier<Object> entitySupplier = () -> entityPersister
        .findOneBy(fieldEntityClass, childJoinColumnName, idValue)
        .orElse(null);
    return ifPresentGetEntityFromContextOrRetrieveFromDb(fieldEntityClass, idValue, entitySupplier);
  }

  private <T> Object resolveOneToManyRelationField(Field entityField, ResultSet rs, T entity) {
    String mappedBy = entityField.getAnnotation(OneToMany.class).mappedBy();
    verifyMappedByIsNotEmpty(mappedBy, entityField.getName());
    if (entityField.getType().equals(List.class)) {
      return resolveOneToManyRelationListField(entityField, rs, entity, mappedBy);
    } else {
      throw NotSupportedException.onlyListIsSupportedForOneToManyRelation(entityField.getName());
    }
  }

  private <T> Object resolveOneToManyRelationListField(Field entityField, ResultSet rs, T entity,
      String mappedBy) {
    Class<?> entityType = entity.getClass();
    Class<?> fieldEntityClass = getGenericListType(entityField);
    Field mappedByField = getMappedByFieldInChildClass(fieldEntityClass, mappedBy);
    String childJoinColumnName = mappedByField.getAnnotation(ManyToOne.class).joinColumnName();
    Object idValue = getEntityIdValueFromResultSet(rs, entityType);

    mappedEntitiesContext.putEntityInContext(entityType, idValue, entity);
    Supplier<Object> entitySupplier = () -> entityPersister
        .findAllBy(fieldEntityClass, childJoinColumnName, idValue);
    return ifPresentGetEntityFromContextOrRetrieveFromDb(fieldEntityClass, idValue, entitySupplier);
  }

  private Class<?> getGenericListType(Field entityField) {
    ParameterizedType genericListType = (ParameterizedType) entityField.getGenericType();
    return (Class<?>) genericListType.getActualTypeArguments()[0];
  }

  private Object resolveSingleRelationalFieldOnChildSide(String joinColumnName, Field entityField, ResultSet rs) {
    verifyJoinColumnNameIsNotEmpty(joinColumnName, entityField.getName());
    Object foreignKeyValue = getValueFromResultSet(rs, joinColumnName);
    Class<?> fieldType = entityField.getType();
    Supplier<Object> entitySupplier = () -> entityPersister.findById(fieldType, foreignKeyValue)
        .orElse(null);
    return ifPresentGetEntityFromContextOrRetrieveAndPut(fieldType, foreignKeyValue, entitySupplier);
  }

  private Object ifPresentGetEntityFromContextOrRetrieveFromDb(Class<?> fieldEntityClass, Object foreignKeyValue,
      Supplier<?> entitySupplier) {
    if (mappedEntitiesContext.isEntityPresentInContext(fieldEntityClass, foreignKeyValue)) {
      return mappedEntitiesContext.getEntityFromContext(fieldEntityClass, foreignKeyValue);
    } else {
      return entitySupplier.get();
    }
  }

  private Object ifPresentGetEntityFromContextOrRetrieveAndPut(Class<?> fieldEntityClass, Object foreignKeyValue,
      Supplier<?> entitySupplier) {
    Object entityValue = ifPresentGetEntityFromContextOrRetrieveFromDb(fieldEntityClass, foreignKeyValue,
        entitySupplier);
    mappedEntitiesContext.putEntityInContext(fieldEntityClass, foreignKeyValue, entityValue);
    return entityValue;
  }

}
