package com.bobocode.blyznytsia.bibernate.mapper;

import static com.bobocode.blyznytsia.bibernate.util.DateUtil.getDateFieldValue;
import static com.bobocode.blyznytsia.bibernate.util.DateUtil.isDateField;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.createEntity;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityFields;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveFieldColumnName;
import static com.bobocode.blyznytsia.bibernate.util.RelationsUtil.isRelationField;
import static com.bobocode.blyznytsia.bibernate.util.ResultSetUtil.getValueFromResultSet;

import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link ResultSetMapper}.
 */
@Slf4j
public class ResultSetMapperImpl implements ResultSetMapper {

  private final RelationResolver relationResolver;

  public ResultSetMapperImpl(EntityPersister entityPersister) {
    this.relationResolver = new RelationResolver(entityPersister);
  }

  /**
   * Maps {@link ResultSet} to entity.
   *
   * @param rs          instance of {@link ResultSet}
   * @param entityClass the type of entity to map
   * @return an entity instance
   */
  @Override
  public <T> T mapToEntity(ResultSet rs, Class<T> entityClass) {
    log.debug("Start parsing ResultSet...");
    T entity = createEntity(entityClass);
    setEntityFields(entity, rs);
    return entity;
  }

  private <T> void setEntityFields(T entity, ResultSet rs) {
    List<Field> entityFields = getEntityFields(entity.getClass());
    entityFields.forEach(entityfield -> setEntityField(entity, entityfield, rs));
  }

  private <T> void setEntityField(T entity, Field entityField, ResultSet rs) {
    logWarningIfFieldIsPrimitive(entityField);
    Object value = getFieldValue(entity, entityField, rs);
    try {
      entityField.setAccessible(true);
      log.debug("Setting value '{}' for entity field '{}'.", value, entityField.getName());
      entityField.set(entity, value);
    } catch (IllegalAccessException ex) {
      throw new BibernateException(String.format(
          "Cannot access or change field %s", entityField.getName()), ex);
    }
  }

  private <T> Object getFieldValue(T entity, Field entityField, ResultSet rs) {
    if (isRelationField(entityField)) {
      return relationResolver.resolveRelationField(entityField, rs, entity);
    } else {
      return getSimpleFieldValue(entityField, rs);
    }
  }

  private Object getSimpleFieldValue(Field entityField, ResultSet rs) {
    Object valueFromResultSet = getValueFromResultSet(rs, resolveFieldColumnName(entityField));
    if (isDateField(entityField.getType())) {
      return getDateFieldValue(entityField, valueFromResultSet);
    }
    return valueFromResultSet;
  }

  private void logWarningIfFieldIsPrimitive(Field entityField) {
    if (entityField.getType().isPrimitive()) {
      log.warn("It's recommended to use an wrapper type for field '{}' instead of primitive.",
          entityField.getName());
    }
  }

}
