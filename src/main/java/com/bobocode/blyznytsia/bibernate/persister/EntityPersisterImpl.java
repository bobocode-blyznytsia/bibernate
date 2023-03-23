package com.bobocode.blyznytsia.bibernate.persister;

import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityNonIdFields;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveEntityIdField;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveEntityTableName;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveFieldColumnName;
import static java.util.stream.Collectors.joining;

import com.bobocode.blyznytsia.bibernate.annotation.Id;
import com.bobocode.blyznytsia.bibernate.exception.PersistenceException;
import com.bobocode.blyznytsia.bibernate.util.EntityUtil;
import com.bobocode.blyznytsia.bibernate.util.ResultSetMapper;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class EntityPersisterImpl implements EntityPersister {

  public static final String SELECT_STATEMENT_TEMPLATE = "SELECT * FROM %s WHERE %s = ?";
  private static final String INSERT_STATEMENT_TEMPLATE = "INSERT INTO %s(%s) VALUES(%s)";
  private static final String UPDATE_STATEMENT_TEMPLATE = "UPDATE %s SET %s WHERE %s = ?";
  private static final String DELETE_STATEMENT_TEMPLATE = "DELETE FROM %s WHERE %s = ?";

  Connection connection;
  ResultSetMapper resultSetMapper;

  public EntityPersisterImpl(Connection connection, ResultSetMapper resultSetMapper) {
    this.connection = connection;
    this.resultSetMapper = resultSetMapper;
  }

  @Override
  public <T> List<T> findAll(Class<T> entityType, Field key, Object value) {
    var entities = new ArrayList<T>();
    var statementText = buildSelectStatement(entityType, key);
    log.debug(statementText);

    try (var statement = connection.prepareStatement(statementText)) {
      statement.setObject(1, value);
      var rs = statement.executeQuery();
      while (rs.next()) {
        entities.add(resultSetMapper.mapToEntity(rs, entityType));
      }
      return entities;
    } catch (SQLException e) {
      throw new PersistenceException("Failed executing SELECT statement", e);
    }
  }

  @Override
  public <T> Optional<T> findOneBy(Class<T> entityType, Field key, Object value) {
    List<T> entitiesFound = findAll(entityType, key, value);
    if (entitiesFound.size() > 1) {
      throw new PersistenceException("Query returned more than one entity");
    }
    return entitiesFound.stream().findFirst();
  }

  @Override
  public <T> Optional<T> findById(Class<T> entityType, Object id) {
    return findOneBy(entityType, EntityUtil.resolveEntityIdField(entityType), id);
  }

  @Override
  public <T> T insert(T entity) {
    Objects.requireNonNull(entity);
    var insertStatementText = buildInsertStatement(entity);
    try (var statement = connection.prepareStatement(insertStatementText, Statement.RETURN_GENERATED_KEYS)) {
      fillInsertWildCards(statement, entity);
      statement.executeUpdate();
      setGeneratedId(entity, statement.getGeneratedKeys());
      return entity;
    } catch (SQLException e) {
      throw new PersistenceException("Failed executing INSERT statement", e);
    }
  }

  public <T> T update(T entity) {
    Objects.requireNonNull(entity);
    var updateStatementText = buildUpdateStatement(entity);
    try (var statement = connection.prepareStatement(updateStatementText)) { //@Todo: see if we can optimize try stmts
      fillUpdateWildCards(statement, entity);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new PersistenceException("Failed executing UPDATE statement", e);
    }
    return entity;
  }

  @Override
  public <T> T delete(T entity) {
    Objects.requireNonNull(entity);
    var deleteStatementText = buildDeleteStatement(entity);
    try (var statement = connection.prepareStatement(deleteStatementText)) {
      fillDeleteWildCard(statement, entity);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new PersistenceException("Failed executing DELETE statement", e);
    }
    return entity;
  }

  @SneakyThrows
  private <T> void fillDeleteWildCard(PreparedStatement statement, T entity) {
    var idField = resolveEntityIdField(entity.getClass());
    idField.setAccessible(true);
    var idValue = idField.get(entity);
    idField.setAccessible(false);
    if (idValue == null) {
      throw new RuntimeException("А-я-яй, більше так не роби");
    }
    statement.setObject(1, idValue);
  }

  private String buildDeleteStatement(Object entity) { //@Todo: consider passing Class<?> insteadof entity itself
    var tableName = resolveEntityTableName(entity.getClass());
    var idFieldName = resolveFieldColumnName(resolveEntityIdField(entity.getClass()));
    return DELETE_STATEMENT_TEMPLATE.formatted(tableName, idFieldName);
  }

  private String buildUpdateStatement(Object entity) {
    var tableName = resolveEntityTableName(entity.getClass());
    var columnAssignments = getEntityNonIdFields(entity).stream()
        .map(EntityUtil::resolveFieldColumnName)
        .map(col -> col + " = ?")
        .collect(joining(", "));
    var idFieldName = resolveFieldColumnName(resolveEntityIdField(entity.getClass()));
    return UPDATE_STATEMENT_TEMPLATE.formatted(tableName, columnAssignments, idFieldName);
  }


  @SneakyThrows
  private void fillUpdateWildCards(PreparedStatement statement, Object entity) {
    var nonIdFields = getEntityNonIdFields(entity).stream()
        .filter(field -> !field.isAnnotationPresent(Id.class))//@Todo: remove and see what happen
        .toList();
    for (int i = 0; i < nonIdFields.size(); i++) {
      var field = nonIdFields.get(i);
      field.setAccessible(true);
      statement.setObject(i + 1, field.get(entity));
      field.setAccessible(false);
    }
    var idField = resolveEntityIdField(entity.getClass());
    idField.setAccessible(true);
    statement.setObject(nonIdFields.size() + 1, idField.get(entity));
    idField.setAccessible(false);
  }

  @SneakyThrows
  private void fillInsertWildCards(PreparedStatement statement, Object entity) {
    var nonIdFields = getEntityNonIdFields(entity).stream()
        .filter(field -> !field.isAnnotationPresent(Id.class))//@Todo: remove and see what happen
        .toList();
    for (int i = 0; i < nonIdFields.size(); i++) {
      var field = nonIdFields.get(i);
      field.setAccessible(true);
      statement.setObject(i + 1, field.get(entity));
      field.setAccessible(false);
    }
  }


  private void setGeneratedId(Object entity, ResultSet generatedKeysRs) throws SQLException {
    generatedKeysRs.next();
    var id = generatedKeysRs.getObject(1);
    var idField = EntityUtil.resolveEntityIdField(entity.getClass());
    idField.setAccessible(true);
    try {
      idField.set(entity, id);
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("We're screwed", e);
    } finally {
      idField.setAccessible(false);
    }
  }

  private String buildInsertStatement(Object entity) {
    var columns = getEntityNonIdFields(entity).stream()
        .map(EntityUtil::resolveFieldColumnName)
        .collect(joining(", "));
    var wildcards = getEntityNonIdFields(entity).stream()
        .map(f -> "?")
        .collect(joining(", "));
    var tableName = resolveEntityTableName(entity.getClass());
    return INSERT_STATEMENT_TEMPLATE.formatted(tableName, columns, wildcards);
  }


  private String buildSelectStatement(Class<?> entityType, Field key) {
    var tableName = resolveEntityTableName(entityType);
    var searchColumnName = resolveFieldColumnName(key);
    return SELECT_STATEMENT_TEMPLATE.formatted(tableName, searchColumnName);
  }


}