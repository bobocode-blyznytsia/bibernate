package com.bobocode.blyznytsia.bibernate.persister;

import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityIdValue;
import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.getEntityNonIdValues;
import static com.bobocode.blyznytsia.bibernate.util.SqlUtil.buildDeleteStatement;
import static com.bobocode.blyznytsia.bibernate.util.SqlUtil.buildInsertStatement;
import static com.bobocode.blyznytsia.bibernate.util.SqlUtil.buildSelectStatement;
import static com.bobocode.blyznytsia.bibernate.util.SqlUtil.buildUpdateStatement;

import com.bobocode.blyznytsia.bibernate.exception.PersistenceException;
import com.bobocode.blyznytsia.bibernate.exception.TransientEntityException;
import com.bobocode.blyznytsia.bibernate.lambda.StatementConsumer;
import com.bobocode.blyznytsia.bibernate.lambda.StatementFunction;
import com.bobocode.blyznytsia.bibernate.mapper.ResultSetMapper;
import com.bobocode.blyznytsia.bibernate.mapper.ResultSetMapperImpl;
import com.bobocode.blyznytsia.bibernate.util.EntityUtil;
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

  private final Connection connection;
  private final ResultSetMapper resultSetMapper;

  public EntityPersisterImpl(Connection connection) {
    this.connection = connection;
    this.resultSetMapper = new ResultSetMapperImpl(this);
  }

  @Override
  public <T> List<T> findAllByQuery(Class<T> entityType, String query, Object... params) {
    var entities = new ArrayList<T>();
    return performWithinStatement(query, statement -> {
      fillStmtWildcards(statement, params);
      var rs = statement.executeQuery();
      while (rs.next()) {
        entities.add(resultSetMapper.mapToEntity(rs, entityType));
      }
      return entities;
    });
  }

  @Override
  public <T> Optional<T> findOneByQuery(Class<T> entityType, String query, Object... params) {
    List<T> entitiesFound = findAllByQuery(entityType, query, params);
    if (entitiesFound.size() > 1) {
      throw new PersistenceException("Query returned more than one entity");
    }
    return entitiesFound.stream().findFirst();
  }

  @Override
  public <T> List<T> findAllBy(Class<T> entityType, String key, Object value) {
    return findAllByQuery(entityType, buildSelectStatement(entityType, key), value);
  }


  @Override
  public <T> Optional<T> findOneBy(Class<T> entityType, String key, Object value) {
    List<T> entitiesFound = findAllBy(entityType, key, value);
    if (entitiesFound.size() > 1) {
      throw new PersistenceException("Query returned more than one entity");
    }
    return entitiesFound.stream().findFirst();
  }

  @Override
  public <T> Optional<T> findById(Class<T> entityType, Object id) {
    Objects.requireNonNull(id, "Entity id must not be null");
    String idColumnName = EntityUtil.resolveFieldColumnName(EntityUtil.resolveEntityIdField(entityType));
    return findOneBy(entityType, idColumnName, id);
  }

  @Override
  public <T> T insert(T entity) {
    Objects.requireNonNull(entity);
    var insertStatementText = buildInsertStatement(entity.getClass());
    return performWithinStatementWithGeneratedKeys(insertStatementText, statement -> {
      fillStmtWildcards(statement, getEntityNonIdValues(entity).toArray());
      statement.executeUpdate();
      setGeneratedId(entity, statement.getGeneratedKeys());
      return entity;
    });
  }

  @Override
  public <T> void update(T entity) {
    Objects.requireNonNull(entity);
    checkEntityNotTransient(entity);
    var updateStatementText = buildUpdateStatement(entity.getClass());
    performWithinStatement(updateStatementText, statement -> {
      var allWildcardFields = new ArrayList<>(getEntityNonIdValues(entity));
      allWildcardFields.add(getEntityIdValue(entity));
      fillStmtWildcards(statement, allWildcardFields.toArray());
      statement.executeUpdate();
    });
  }

  @Override
  public <T> void delete(T entity) {
    Objects.requireNonNull(entity);
    checkEntityNotTransient(entity);
    var deleteStatementText = buildDeleteStatement(entity.getClass());
    performWithinStatement(deleteStatementText, statement -> {
      fillStmtWildcards(statement, getEntityIdValue(entity));
      statement.executeUpdate();
    });
  }

  private <T> T performWithinStatementWithGeneratedKeys(String statement, StatementFunction<T> function) {
    return performWithinStatement(statement, true, function);
  }

  private void performWithinStatement(String statement, StatementConsumer consumer) {
    performWithinStatement(statement, false, consumer);
  }

  private <T> T performWithinStatement(String statement, StatementFunction<T> function) {
    return performWithinStatement(statement, false, function);
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

  private void checkEntityNotTransient(Object entity) {
    var id = getEntityIdValue(entity);
    if (id == null) {
      throw new TransientEntityException("This operation cannot be performed with transient entity");
    }
  }


}
