package com.bobocode.blyznytsia.bibernate;

import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayNameGenerator;

public class CamelCaseNameGenerator extends DisplayNameGenerator.Simple {
  @Override
  public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
    return testMethod.getName().replaceAll("([a-z])([A-Z])","$1 $2").toLowerCase();
  }
}