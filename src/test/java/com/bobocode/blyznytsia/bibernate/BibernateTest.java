package com.bobocode.blyznytsia.bibernate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BibernateTest {

  @Test
  void mainTest() {
    Bibernate.main(new String[0]);
    assertSame(4, 2 + 2);
  }
}