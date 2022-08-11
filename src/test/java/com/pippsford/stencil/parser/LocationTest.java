package com.pippsford.stencil.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 06/01/2021.
 */
class LocationTest {

  static final String text = "abcdefghij\n0123456789\r\nABCDE\rZYXWY";


  @Test
  public void test1() {
    Location location = new Location(text, text.indexOf('Y'));
    assertEquals(1, location.getColumn());
    assertEquals(4, location.getRow());
  }


  @Test
  public void test2() {
    Location location = new Location(text, text.indexOf('9'));
    assertEquals(9, location.getColumn());
    assertEquals(2, location.getRow());
  }

}