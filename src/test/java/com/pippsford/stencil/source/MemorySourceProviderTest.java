package com.pippsford.stencil.source;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 12/01/2021.
 */
class MemorySourceProviderTest {

  @Test
  public void coverage() {
    assertNotNull(new MemorySourceProvider().toString());
  }

}