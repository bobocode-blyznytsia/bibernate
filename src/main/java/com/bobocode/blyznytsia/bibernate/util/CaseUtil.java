package com.bobocode.blyznytsia.bibernate.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for case conversions
 */
@UtilityClass
public class CaseUtil {
  /**
   * Converts a string in camelCase format to snake_case format.
   *
   * @param str the camelCase string to be converted
   * @return the snake_case string
   */
  public static String camelToSnakeCase(String str) {
    return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
  }

}
