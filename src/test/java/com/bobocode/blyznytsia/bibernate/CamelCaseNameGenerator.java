package com.bobocode.blyznytsia.bibernate;

import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayNameGenerator;

public class CamelCaseNameGenerator extends DisplayNameGenerator.Simple {
  @Override
  public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
    return capitalizeFirstWord(splitWords(testMethod.getName()));
  }

  String splitWords(String str) {
    return str.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
  }

  String capitalizeFirstWord(String str) {
    return Character.toUpperCase(str.charAt(0)) + str.substring(1);
  }
}
