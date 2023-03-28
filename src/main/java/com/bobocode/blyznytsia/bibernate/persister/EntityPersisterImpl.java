package com.bobocode.blyznytsia.bibernate.persister;

import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityIdValue;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityNonIdValues;
import static com.bobocode.blyznytsia.bibernate.util.SqlUtil.buildDeleteStatement;
import static com.bobocode.blyznytsia.bibernate.util.SqlUtil.buildInsertStatement;
import static com.bobocode.blyznytsia.bibernate.util.SqlUtil.buildSelectStatement;
import static com.bobocode.blyznytsia.bibernate.util.SqlUtil.buildUpdateStatement;

import com.bobocode.blyznytsia.bibernate.exception.PersistenceException;
import com.bobocode.blyznytsia.bibernate.lambda.StatementConsumer;
import com.bobocode.blyznytsia.bibernate.lambda.StatementFunction;
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
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class EntityPersisterImpl implements EntityPersister {

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
    return performWithinStatement(statementText, false, statement -> {
      fillSelectStatement(statement, value);
      var rs = statement.executeQuery();
      while (rs.next()) {
        entities.add(resultSetMapper.mapToEntity(rs, entityType));
      }
      return entities;
    });
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
    if (id == null) {
      throw new IllegalArgumentException("Id must not be null");
    }
    return findOneBy(entityType, EntityUtil.resolveEntityIdField(entityType), id);
  }

  @Override
  public <T> T insert(T entity) {
    Objects.requireNonNull(entity);
    var insertStatementText = buildInsertStatement(entity.getClass());
    return performWithinStatement(insertStatementText, true, statement -> {
      fillInsertStatement(statement, entity);
      statement.executeUpdate();
      setGeneratedId(entity, statement.getGeneratedKeys());
      return entity;
    });
  }

  public void update(Object entity) {
    Objects.requireNonNull(entity);
    var updateStatementText = buildUpdateStatement(entity.getClass());
    performWithinStatement(updateStatementText, statement -> {
      fillUpdateStatement(statement, entity);
      statement.executeUpdate();
    });
  }

  @Override
  public void delete(Object entity) {
    Objects.requireNonNull(entity);
    var deleteStatementText = buildDeleteStatement(entity.getClass());
    performWithinStatement(deleteStatementText, statement -> {
      fillDeleteStatement(statement, entity);
      statement.executeUpdate();
    });
  }

  private void performWithinStatement(String statement, StatementConsumer consumer) {
    performWithinStatement(statement, false, consumer);
  }

  private <T> T performWithinStatement(String statementText, boolean generatedKeys, StatementFunction<T> function) {
    log.info("Executing SQL statement: {}", statementText);
    try (var statement = connection.prepareStatement(statementText,
        generatedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS)
    ) {
      return function.apply(statement);
    } catch (SQLException e) {
      throw new PersistenceException("Failed performing SQL statement: " + statementText, e);
    }
  }

  private void fillSelectStatement(PreparedStatement statement, Object value) throws SQLException {
    fillStmtWildcards(statement, value);
  }

  private <T> void fillDeleteStatement(PreparedStatement statement, T entity) throws SQLException {
    var id = getEntityIdValue(entity);
    if (id == null) {
      throw new PersistenceException("Cannot delete transient entity");
    }
    fillStmtWildcards(statement, id);
  }

  private void fillUpdateStatement(PreparedStatement statement, Object entity) throws SQLException {
    var allWildcardFields = new ArrayList<>(getEntityNonIdValues(entity));
    var id = getEntityIdValue(entity);
    if (id == null) {
      throw new PersistenceException("Cannot update transient entity");
    }
    allWildcardFields.add(getEntityIdValue(entity));
    fillStmtWildcards(statement, allWildcardFields.toArray());
  }

  private void fillInsertStatement(PreparedStatement statement, Object entity) throws SQLException {
    fillStmtWildcards(statement, getEntityNonIdValues(entity).toArray());
  }

  private void fillStmtWildcards(PreparedStatement statement, Object... values) throws SQLException {
    for (var i = 0; i < values.length; i++) {
      statement.setObject(i + 1, values[i]);
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
      throw new PersistenceException("Unable to assign a generated key for the entity", e);
    }
  }


}
