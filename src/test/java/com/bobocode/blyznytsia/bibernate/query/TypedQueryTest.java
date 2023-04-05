package com.bobocode.blyznytsia.bibernate.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bobocode.blyznytsia.bibernate.CamelCaseNameGenerator;
import com.bobocode.blyznytsia.bibernate.exception.MissingParamsException;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import com.bobocode.blyznytsia.bibernate.testdata.SampleEntity;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(CamelCaseNameGenerator.class)
class TypedQueryTest {

  @Mock
  EntityPersister mockEntityPersister;
  Class<SampleEntity> entityType;

  @Test
  void convertsNativeQueryToJdbcCompliantQuery() throws ReflectiveOperationException {
    var sqlWithParams =
        "SELECT * FROM sample where a = :namedParam1 AND b = :named-param2 AND c = ?1 AND d = ?2 AND e = a:named_param3";
    var properJdbcSql = "SELECT * FROM sample where a = ? AND b = ? AND c = ? AND d = ? AND e = a?";
    var typedQuery = new TypedQuery<>(entityType, sqlWithParams, mockEntityPersister);
    var compiledQueryField = TypedQuery.class.getDeclaredField("jdbcQuery");
    compiledQueryField.setAccessible(true);

    assertEquals(properJdbcSql, compiledQueryField.get(typedQuery));
  }

  @Nested
  @TestMethodOrder(MethodOrderer.MethodName.class)
  class SetParam {
    @Test
    void setsNamedParameters() {
      var sql = "SELECT * FROM sample where id = :id and name = :name";
      var idVal = "idVal";
      var nameVal = "nameVal";
      var expectedParams = new Object[] {idVal, nameVal};

      new TypedQuery<>(entityType, sql, mockEntityPersister)
          .setParam("name", nameVal)
          .setParam("id", idVal)
          .getSingleResult();

      verify(mockEntityPersister).findOneByQuery(eq(entityType), any(), eq(expectedParams));
    }

    @Test
    void setsOrdinalParameters() {
      var sql = "SELECT * FROM sample where id = ?1 and name = ?2";
      var param1Val = "idVal";
      var param2Val = "nameVal";
      var expectedParams = new Object[] {param1Val, param2Val};

      new TypedQuery<>(entityType, sql, mockEntityPersister)
          .setParam(1, param1Val)
          .setParam(2, param2Val)
          .getSingleResult();

      verify(mockEntityPersister).findOneByQuery(eq(entityType), any(), eq(expectedParams));
    }

    @Test
    void setsMixedParameters() {
      var sql = "SELECT * FROM sample where id = :id and name = ?1";
      var namedParam = "idVal";
      var ordinalParam = "nameVal";
      var expectedParams = new Object[] {namedParam, ordinalParam};

      new TypedQuery<>(entityType, sql, mockEntityPersister)
          .setParam("id", namedParam)
          .setParam(1, ordinalParam)
          .getSingleResult();

      verify(mockEntityPersister).findOneByQuery(eq(entityType), any(), eq(expectedParams));
    }

    @Test
    void throwsIllegalArgumentExceptionWhenNamedParamDoesNotExists() {
      var sql = "SELECT * FROM sample where id = :id";
      var typedQuery = new TypedQuery<>(entityType, sql, mockEntityPersister);
      assertThrows(IllegalArgumentException.class, () -> typedQuery.setParam("nonExistingParam", "anyVal"));
    }

    @Test
    void throwsIllegalArgumentExceptionWhenOrdinalParamDoesNotExists() {
      var sql = "SELECT * FROM sample where id = ?1";
      var typedQuery = new TypedQuery<>(entityType, sql, mockEntityPersister);
      assertThrows(IllegalArgumentException.class, () -> typedQuery.setParam(100, "anyVal"));
    }
  }

  @Nested
  class GetSingleResult {
    @Test
    void returnsEntity() {
      var sql = "SELECT * FROM sample where id = :id";
      var idParam = "idVal";
      var expectedEntity = new SampleEntity(1L, "val");
      when(mockEntityPersister.findOneByQuery(SampleEntity.class, "SELECT * FROM sample where id = ?", idParam))
          .thenReturn(Optional.of(expectedEntity));

      var actualEntityOptional = new TypedQuery<>(SampleEntity.class, sql, mockEntityPersister)
          .setParam("id", idParam)
          .getSingleResult();

      assertTrue(actualEntityOptional.isPresent());
      assertSame(expectedEntity, actualEntityOptional.get());
    }

    @Test
    void throwsMissingParamsExceptionIfParameterIsNotSet() {
      var sql = "SELECT * FROM sample WHERE id= :paramThatWillNotBeSet";
      var typedQuery = new TypedQuery<>(entityType, sql,mockEntityPersister);
      assertThrows(MissingParamsException.class, typedQuery::getSingleResult);
    }
  }

  @Nested
  class GetResultList {
    @Test
    void returnsEntity() {
      var sql = "SELECT * FROM sample where id = :id";
      var idParam = "idVal";
      var expectedEntity = new SampleEntity(1L, "val");
      when(mockEntityPersister.findAllByQuery(SampleEntity.class, "SELECT * FROM sample where id = ?", idParam))
          .thenReturn(List.of(expectedEntity));

      var actualEntities = new TypedQuery<>(SampleEntity.class, sql, mockEntityPersister)
          .setParam("id", idParam)
          .getResultList();

      assertSame(expectedEntity, actualEntities.get(0));
    }

    @Test
    void throwsMissingParamsExceptionIfParameterIsNotSet() {
      var sql = "SELECT * FROM sample WHERE id= :paramThatWillNotBeSet";
      var typedQuery = new TypedQuery<>(entityType, sql,mockEntityPersister);
      assertThrows(MissingParamsException.class, typedQuery::getResultList);
    }
  }

}