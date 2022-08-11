package com.pippsford.stencil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.escape.StandardEscape;
import com.pippsford.stencil.source.MemorySourceProvider;

/**
 * @author Simon Greatrix on 12/01/2021.
 */
class StencilsTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  public void test1() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test", "{value}");
    Map map = Map.of("value", "<\n>");
    assertEquals(StandardEscape.HTML_SAFE, stencils.getDefaultEscape());
    assertEquals("&lt;\n&gt;", stencils.write("test", map));
    stencils.setDefaultEscape(StandardEscape.JSON);
    assertEquals(StandardEscape.JSON, stencils.getDefaultEscape());
    assertEquals("<\\n>", stencils.write("test", map));
  }


  @Test
  public void test2() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test", "[{message}]");
    assertNull(stencils.getDefaultResourceBundle());
    stencils.setDefaultResourceBundle("test_messages");
    assertEquals("test_messages", stencils.getDefaultResourceBundle());
    assertEquals("Some words", stencils.write("test", null));

    stencils.setDefaultResourceBundle("test_stencils");
    assertEquals("test_stencils", stencils.getDefaultResourceBundle());
    assertEquals("Some other words.", stencils.write("test", null));
  }


  @Test
  public void test3() {
    assertNull(stencils.getDefaultValue("foo"));
    stencils.setDefaultValue("foo", "bar");
    assertEquals("bar", stencils.getDefaultValue("foo"));
    stencils.setDefaultValue("foo", null);
    assertNull(stencils.getDefaultValue("foo"));
  }

}