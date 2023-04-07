package com.bobocode.blyznytsia.bibernate.repository;

import com.bobocode.blyznytsia.bibernate.exception.RepositoryException;
import com.bobocode.blyznytsia.bibernate.query.MethodNameToQueryParser;
import com.bobocode.blyznytsia.bibernate.query.Query;
import com.bobocode.blyznytsia.bibernate.session.SessionFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepositoryProxyInvocationHandler<T> implements InvocationHandler {
  private final MethodNameToQueryParser sqlQueryBuilder;
  private final SessionFactory sessionFactory;
  private final Class<T> entityType;

  public RepositoryProxyInvocationHandler(Class<T> entityType, SessionFactory sessionFactory) {
    this.sqlQueryBuilder = new MethodNameToQueryParser(entityType);
    this.sessionFactory = sessionFactory;
    this.entityType = entityType;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] params) {
    validateMethodDeclaration(method);
    log.debug("Running repository autogenerated query");
    var query = buildNativeQuery(method, params);
    if (method.getName().startsWith("finOneBy")) {
      return query.getSingleResult();
    } else {
      return query.getResultList();
    }
  }

  private void validateMethodDeclaration(Method method) {
    var returnType = method.getReturnType();
    var methodName = method.getName();
    if (methodName.startsWith("findOneBy") && returnType.equals(Optional.class)) {
      return;
    }
    if (methodName.startsWith("findAllBy") && returnType.equals(List.class)) {
      return;
    }
    throw new RepositoryException(
        "Invalid method declaration: make sure the method name align with convention and the return type is supported");
  }

  private Query<T> buildNativeQuery(Method method, Object[] params) {
    var sqlQuery = sqlQueryBuilder.generateQueryByName(method.getName());
    var session = sessionFactory.openSession();
    var query = session.createNativeQuery(sqlQuery, entityType);
    if (params.length != query.getParamsCount()) {
      throw new RepositoryException("Count of method parameters does not matches count method arguments");
    }
    for (var i = 0; i < params.length; i++) {
      query.setParam(i + 1, params[i]);
    }
    return query;
  }
}
