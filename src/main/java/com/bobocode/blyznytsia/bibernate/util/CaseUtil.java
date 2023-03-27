package com.bobocode.blyznytsia.bibernate.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CaseUtil {

  public static String camelToSnakeCase(String str) {
    return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
  }

}
