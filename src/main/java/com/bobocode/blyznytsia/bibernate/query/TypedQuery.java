package com.bobocode.blyznytsia.bibernate.query;

import com.bobocode.blyznytsia.bibernate.exception.MissingParamsException;
import com.bobocode.blyznytsia.bibernate.persister.EntityPersister;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypedQuery<T> implements Query<T> {
  private static final String PARAM_REGEX = "((\\?\\d+)|(\\:[a-zA-Z\\-\\_\\d]+))";
  private final EntityPersister entityPersister;
  private final String jdbcQuery;
  private final Class<T> entityType;
  private Object[] params;
  Map<String, Integer> namedParamToPosition;
  Map<Integer, Integer> ordinalParamToPosition;

  public TypedQuery(Class<T> entityType, String query, EntityPersister entityPersister) {
    this.entityPersister = entityPersister;
    this.entityType = entityType;
    this.jdbcQuery = buildJdbcQuery(query);
    initParamsPlaceholders(query);
  }

  @Override
  public TypedQuery<T> setParam(String namedParam, Object value) {
    if (!namedParamToPosition.containsKey(namedParam)) {
      throw new IllegalArgumentException("There is no named parameter '%s' within the query".formatted(namedParam));
    }
    params[namedParamToPosition.get(namedParam)] = value;
    return this;
  }

  @Override
  public TypedQuery<T> setParam(int ordinalParamIndex, Object value) {
    if (!ordinalParamToPosition.containsKey(ordinalParamIndex)) {
      throw new IllegalArgumentException(
          "There is no parameter with index %d within the query".formatted(ordinalParamIndex));
    }
    params[ordinalParamToPosition.get(ordinalParamIndex)] = value;
    return this;
  }

  @Override
  public Optional<T> getSingleResult() {
    verifyAllParamsAreSet();
    return entityPersister.findOneByQuery(entityType, jdbcQuery, params);
  }

  @Override
  public List<T> getResultList() {
    verifyAllParamsAreSet();
    return entityPersister.findAllByQuery(entityType, jdbcQuery, params);
  }

  @Override
  public int getParamsCount() {
    return params.length;
  }

  private void verifyAllParamsAreSet() {
    var allParamsSet = Arrays.stream(params)
        .noneMatch(Objects::isNull);
    if (!allParamsSet) {
      throw new MissingParamsException("Some parameters for the query were not set");
    }
  }

  private void initParamsPlaceholders(String query) {
    namedParamToPosition = new HashMap<>();
    ordinalParamToPosition = new HashMap<>();
    var currentParamPos = 0;
    var matcher = Pattern.compile(PARAM_REGEX).matcher(query);
    log.debug("Resolving query params...");
    while (matcher.find()) {
      var param = matcher.group(1);
      log.trace("Found new parameter : {}", params);
      if (param.startsWith(":")) {
        namedParamToPosition.put(param.substring(1), currentParamPos++);
      } else {
        ordinalParamToPosition.put(Integer.parseInt(param.substring(1)), currentParamPos++);
      }
    }
    log.debug("{} params where found in query", currentParamPos);
    params = new Object[currentParamPos];
  }

  private String buildJdbcQuery(String query) {
    var compiledQuery = query.replaceAll(PARAM_REGEX, "?");
    log.debug("Query compiled: {}", compiledQuery);
    return compiledQuery;
  }


}
