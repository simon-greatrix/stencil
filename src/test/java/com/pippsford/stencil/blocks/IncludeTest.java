package com.pippsford.stencil.blocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.parser.StencilParseFailedException;
import com.pippsford.stencil.source.MemorySourceProvider;
import com.pippsford.stencil.apply.ValueProcessor;

/**
 * @author Simon Greatrix on 12/01/2021.
 */
class IncludeTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  void notInMessages() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[include header.txt]\nHello, World!\n[{test_messages, forbidden_include}]");
    sourceProvider.putFile(Locale.ROOT, "header.txt", "Header!");

    assertThrows(StencilParseFailedException.class, () -> stencils.write("/test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), null));
  }


  @Test
  public void recurse() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "recurse.txt", "{count} [if count][apply F.decrement][include recurse.txt][end]");
    stencils.setDefaultValue("F.decrement", (ValueProcessor) (d, a) -> {
      d.put("count", ((Integer) d.get("count")) - 1);
      return null;
    });
    Map<String, Object> map = Map.of("count", 5);

    String output = stencils.write("recurse.txt", Locale.FRANCE, ZoneId.of("Europe/London"), map);
    assertEquals("5 4 3 2 1 0 ", output);
  }


  @Test
  void test() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[include header.txt]\nHello, World!\n[include footer.txt]");
    sourceProvider.putFile(Locale.ROOT, "header.txt", "Header!");
    sourceProvider.putFile(Locale.ROOT, "footer.txt", "Footer!");

    String output = stencils.write("test.txt", Locale.FRANCE, ZoneId.of("Europe/London"), "");
    assertEquals("Header!\n"
        + "Hello, World!\n"
        + "Footer!", output);
  }

}