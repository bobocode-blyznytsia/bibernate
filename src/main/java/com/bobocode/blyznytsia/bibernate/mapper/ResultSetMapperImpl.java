package com.bobocode.blyznytsia.bibernate.mapper;

import static com.bobocode.blyznytsia.bibernate.util.CaseUtil.*;

import com.bobocode.blyznytsia.bibernate.annotation.Column;
import com.bobocode.blyznytsia.bibernate.annotation.ManyToOne;
import com.bobocode.blyznytsia.bibernate.annotation.OneToMany;
import com.bobocode.blyznytsia.bibernate.annotation.OneToOne;
import com.bobocode.blyznytsia.bibernate.exception.BibernateException;
import com.bobocode.blyznytsia.bibernate.exception.MalformedEntityException;
import com.bobocode.blyznytsia.bibernate.session.SessionImpl;
import com.bobocode.blyznytsia.bibernate.util.EntityUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.sql.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ResultSetMapperImpl implements ResultSetMapper {

  private final SessionImpl session;

  @Override
  public <T> T mapToEntity(ResultSet resultSet, Class<T> entityClass) {
    log.debug("Start parsing ResultSet.");
    //1. CREATE OBJECT WITH DEFAULT CONSTRUCTOR
    //2. READ FIELDS OF OBJECT
    //3. SET VALUES TO FIELD
    T entity = createEntity(entityClass);
    setEntityFields(entity, resultSet);
    return entity;
  }

  private <T> void setEntityFields(T entity, ResultSet resultSet) {
    try {
      if(resultSet.next()) {
        List<EntityFiled> entityFields = getEntityFields(entity);
        for(EntityFiled entityFiled : entityFields) {
          setEntityFiled(entity, entityFiled, resultSet);
        }
      } else {
        throw new BibernateException("ResultSet is empty cannot find entity");
      }
    } catch (SQLException e) {
      throw new BibernateException("Cannot read from ResultSet"); //TODO: update here
    }
  }

  private <T> void setEntityFiled(T entity, EntityFiled entityFiled, ResultSet resultSet) {
    printWarningIfFieldIsPrimitive(entityFiled);
    Object value = null;
    if(isRelationalField(entityFiled)) {
      value = setRelationalField(entity.getClass(), entityFiled, resultSet);
    }
    else {
      value = getValueFromResultSet(resultSet, entityFiled.dbColumnName);
      if (isDateField(entityFiled.objectFiled.getType())) {
        value = setDateField(entityFiled, value);
      }
    }
    try {
      entityFiled.objectFiled.setAccessible(true);
      log.debug("Setting value '{}' for entity field '{}'.", value, entityFiled.objectFiled.getName());
      entityFiled.objectFiled.set(entity, value);
    } catch (IllegalAccessException e) {
      throw new BibernateException(String.format("Cannot access or change field %s",
          entityFiled.objectFiled.getName()), e);
    }
  }

  private Object setRelationalField(Class<?> entityType, EntityFiled entityFiled, ResultSet resultSet) {
    Field objField = entityFiled.objectFiled;
    if(objField.isAnnotationPresent(ManyToOne.class)) { //один інстанс
      String joinColumnName = objField.getAnnotation(ManyToOne.class).joinColumnName();
      return getRelationalFieldValueForSingleLookup(joinColumnName, objField, resultSet);
    } else if(objField.isAnnotationPresent(OneToOne.class)) {
      String joinColumnName = objField.getAnnotation(OneToOne.class).joinColumnName();
      return getRelationalFieldValueForSingleLookup(joinColumnName, objField, resultSet);
    } else if(objField.isAnnotationPresent(OneToMany.class)) {
      String joinColumnName = objField.getAnnotation(OneToMany.class).joinColumnName();
      if(!joinColumnName.isEmpty()) {
        log.warn("It's recommended to specify join column on the child side in class {}.",
            objField.getName());
        //do:
      } else {
        String mappedBy = objField.getAnnotation(OneToMany.class).mappedBy();
        //TODO: MAPBY REQUIRED EXCEPTION
        if(entityFiled.objectFiled.getType().equals(List.class)) {
          ParameterizedType genericListType = (ParameterizedType) objField.getGenericType();
          Class<?> fieldEntityClass = (Class<?>) genericListType.getActualTypeArguments()[0];

          Field mappedByField = Arrays.stream(fieldEntityClass.getDeclaredFields())
              .filter(field -> field.getName().equals(mappedBy))
                  .findFirst()
                      .orElseThrow(() -> new BibernateException(String.format("Field %s that was specified in mappedBy"
                          + "doesn't exist in class %s", mappedBy, fieldEntityClass.getName())));
          String childJoinColumnName = mappedByField.getAnnotation(ManyToOne.class).joinColumnName();

          Field idField = EntityUtil.resolveEntityIdField(entityType);
          String idDbColumnName = getDbColumnName(idField);
          Object idValue = getValueFromResultSet(resultSet, idDbColumnName);

          List<Object> value = session.findAllBy(fieldEntityClass, childJoinColumnName, idValue);
          return value;
        } else {
          throw new BibernateException("Only list type is supported in @OneToMany relation.");
        }
        //List<Object> values = session.findAllBy()
        //select * from notes where user_id = 1
      }
    } else {
      throw new BibernateException("Relation is not supported");
    }
  }

  private Object getRelationalFieldValueForSingleLookup(String joinColumnName, Field objField, ResultSet resultSet) {
    verifyJoinColumnNameIsNotEmpty(joinColumnName, objField);
    Object foreignKeyValue = getValueFromResultSet(resultSet, joinColumnName);
    Class<?> fieldType = objField.getType();
    Object value = session.find(fieldType, foreignKeyValue);
    return value;
  }

  private void verifyJoinColumnNameIsNotEmpty(String joinColumnName, Field entityFiled) {
    if(joinColumnName == null || joinColumnName.isEmpty()) {
      throw new BibernateException(
          String.format("Join column name must be set for field %s", entityFiled.getName()
      ));
    }
  }

  private boolean isRelationalField(EntityFiled entityFiled) {
    Field objField = entityFiled.objectFiled;
    return objField.isAnnotationPresent(ManyToOne.class)
        || objField.isAnnotationPresent(OneToMany.class)
        || objField.isAnnotationPresent(OneToOne.class);
  }

  private void printWarningIfFieldIsPrimitive(EntityFiled entityFiled) {
    if(entityFiled.objectFiled.getType().isPrimitive()) {
      log.warn("It's recommended to use an wrapper type for field '{}' instead of primitive.",
          entityFiled.objectFiled.getName());
    }
  }

  private boolean isDateField(Class<?> fieldType) {
    if(fieldType.getName().contains("java.time")) {
      if(fieldType.equals(LocalDateTime.class) || fieldType.equals(LocalDate.class)
          || fieldType.equals(LocalTime.class) || fieldType.equals(Instant.class)) {
        return true;
      } else {
        throw new BibernateException(String.format("Type %s is not supported by Bibernate framework", fieldType));
      }
    } else {
      return false;
    }
  }

  private Object setDateField(EntityFiled entityFiled, Object value) {
    log.debug("Convert date field '{}' to set field type {} with value {}.",
        entityFiled.objectFiled.getName(), entityFiled.objectFiled.getType(), value);
    return switch (value) {
      case Timestamp ignore -> getFieldValueForLocalDateTimeDependsOnField(
          entityFiled, ((Timestamp) value));
      case Date ignore -> ((java.sql.Date) value).toLocalDate();
      default -> value;
    };
  }

  private Object getFieldValueForLocalDateTimeDependsOnField(EntityFiled entityFiled,
      Timestamp timestamp) {
    Class<?> fieldType = entityFiled.objectFiled.getType();
    if(fieldType.equals(LocalDateTime.class)) {
      return timestamp.toLocalDateTime();
    } else if(fieldType.equals(LocalDate.class)) {
      return timestamp.toLocalDateTime().toLocalDate();
    } else if(fieldType.equals(LocalTime.class)) {
      return timestamp.toLocalDateTime().toLocalTime();
    } else if(fieldType.equals(Instant.class)) {
      return timestamp.toInstant();
    } else {
      return timestamp;
    }
  }

  private Object getValueFromResultSet(ResultSet resultSet, String columnName) {
    try {
      return resultSet.getObject(columnName);
    } catch (SQLException ex) {
      throw new MalformedEntityException(
          String.format("Cannot find column with name %s...", columnName));
    }
  }

  private <T> List<EntityFiled> getEntityFields(T entity) {
    Class<?> entityClass = entity.getClass();
    return Arrays.stream(entityClass.getDeclaredFields())
        .map(this::createEntityField)
        .toList();
  }

  private EntityFiled createEntityField(Field field) {
    return new EntityFiled(
        getDbColumnName(field),
        field
    );
  }

  private String getDbColumnName(Field field) {
    if(field.isAnnotationPresent(Column.class)) {
      return field.getAnnotation(Column.class).name();
    } else {
      return camelToSnakeCase(field.getName());
    }
  }

  private record EntityFiled(String dbColumnName, Field objectFiled) {

  }

  private <T> T createEntity(Class<T> entityClass) {
    Constructor<?> constructor = getDefaultConstructor(entityClass);
    try {
      log.debug("Creating entity instance.");
      return (T) constructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
      throw new MalformedEntityException(String.format("Cannot create entity of class %s.",
          entityClass.getSimpleName()));
    }
  }

  private <T> Constructor<?> getDefaultConstructor(Class<T> entityClass) {
    log.debug("Getting default constructor for type {}.", entityClass.getName());
    return Arrays.stream(entityClass.getDeclaredConstructors())
        .filter(c -> c.getParameterCount() == 0)
        .findFirst()
        .orElseThrow(() -> new MalformedEntityException(
            String.format("Constructor with no params is required in class %s.", entityClass.getSimpleName())));
  }


}
