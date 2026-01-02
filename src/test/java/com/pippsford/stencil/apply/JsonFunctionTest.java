package com.pippsford.stencil.apply;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.pippsford.json.CJObject;
import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;
import com.pippsford.stencil.source.MemorySourceProvider;
import org.junit.jupiter.api.Test;

class JsonFunctionTest {

  MemorySourceProvider sourceProvider = new MemorySourceProvider();

  Stencils stencils = new Stencils(sourceProvider);


  @Test
  void test0() throws StencilException {
    sourceProvider.putFile(Locale.ROOT, "test.txt", "[apply b=F.json(map)]{none:b}");
    Map<String, Object> map = Map.of(
        "map", Map.of(
            "v1", "A",
            "n0", 8,
            "flag", true,
            "o", Map.of("a", "b"),
            "a", List.of(1, 2, 3),
            "j", new CJObject(Map.of("x","y"))
        )
    );

    String text = stencils.write("test.txt", Locale.ROOT, ZoneId.of("Europe/London"), map);
    assertEquals(
        """
            {"a":[1,2,3],"flag":true,"j":{"x":"y"},"n0":8,"o":{"a":"b"},"v1":"A"}""", text
    );
  }

}
