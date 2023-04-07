package com.bobocode.blyznytsia.bibernate.query;

import static com.bobocode.blyznytsia.bibernate.util.EntityUtil.resolveEntityTableName;

import com.bobocode.blyznytsia.bibernate.exception.RepositoryGenerationException;
import com.bobocode.blyznytsia.bibernate.util.EntityUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * A helper class that generates native SQL queries by parsing method names
 * that follow the Spring Data JPA naming convention
 */
@Slf4j
public class MethodNameToQueryParser {
  private static final int WHERE_CLAUSE_POS = 0;
  private static final int ORDER_BY_CLAUSE_POS = 1;
  Class<?> entityType;

  public MethodNameToQueryParser(Class<?> entityType) {
    this.entityType = entityType;
  }

  public String generateQueryByName(String methodName) {
    var strWithoutFindBy = methodName.substring(9);
    var tokens = strWithoutFindBy.split("OrderBy");
    var queryBuilder = new QueryBuilder(resolveEntityTableName(entityType));
    parseWhereTokens(tokens[WHERE_CLAUSE_POS], queryBuilder);
    if (tokens.length > 1) {
      parseOrderByTokens(tokens[ORDER_BY_CLAUSE_POS], queryBuilder);
    }
    return queryBuilder.buildNativeSqlQuery();
  }

  private void parseOrderByTokens(String orderClauseTokens, QueryBuilder queryBuilder) {
    if (orderClauseTokens.isBlank()) {
      return;
    }
    queryBuilder.orderBy(resolveColumnName(orderClauseTokens, "Desc"));
    if (orderClauseTokens.endsWith("Desc")) {
      queryBuilder.descending();
    }
  }

  private void parseWhereTokens(String whereClauseTokens, QueryBuilder builder) {
    List<String> tokens = new ArrayList<>();
    var matcher = Pattern.compile("(?<=And|Or)|(?=And|Or|$)").matcher(whereClauseTokens);
    var start = 0;
    while (matcher.find()) {
      int end = matcher.start();
      if (end != 0) {
        tokens.add(whereClauseTokens.substring(start, end));
        start = end;
      }
    }
    tokens.forEach(token -> parseSingleWhereToken(token, builder));
  }

  private void parseSingleWhereToken(String token, QueryBuilder builder) {
    log.trace("processing token : " + token);
    if (token == null || token.isBlank()) {
      throw new RepositoryGenerationException("invalid token value");
    } else if (token.equals("And")) {
      builder.and();
    } else if (token.equals("Or")) {
      builder.or();
    } else if (token.endsWith("IsNotNull") || token.endsWith("NotNull")) {
      builder.isNotNull(resolveColumnName(token, "(IsNotNull|NotNull)"));
    } else if (token.endsWith("IsNull") || token.endsWith("Null")) {
      builder.isNotNull(resolveColumnName(token, "(IsNull|Null)"));
    } else {
      builder.isEqual(resolveColumnName(token, "(Is|Equals)"));
    }
  }


  private String resolveColumnName(String token, String keywordPattern) {
    var tokenWithoutKeyword = token.split(keywordPattern)[0];
    var fieldName = decapitalize(tokenWithoutKeyword);
    try {
      var entityField = entityType.getDeclaredField(fieldName);
      return EntityUtil.resolveFieldColumnName(entityField);
    } catch (NoSuchFieldException e) {
      throw new RepositoryGenerationException(
          "Entity of type " + entityType.getName() + "does not contain field " + fieldName);
    }
  }

  private String decapitalize(String str) {
    return str.substring(0, 1).toLowerCase() + str.substring(1);
  }

}
