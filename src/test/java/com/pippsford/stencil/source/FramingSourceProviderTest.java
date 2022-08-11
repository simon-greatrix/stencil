package com.pippsford.stencil.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Locale;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.Source;

/**
 * @author Simon Greatrix on 13/02/2021.
 */
class FramingSourceProviderTest {

  BiFunction<Source, Locale, String> footer = (s, l) -> "FOOT:" + l;

  BiFunction<Source, Locale, String> header = (s, l) -> "HEAD:" + l + "\n";

  MemorySourceProvider memory = new MemorySourceProvider();

  FramingSourceProvider sourceProvider = new FramingSourceProvider(memory);


  @Test
  void getFooterProvider() {
    assertNotSame(header, sourceProvider.getFooterProvider());
    sourceProvider.setFooterProvider(header);
    assertSame(header, sourceProvider.getFooterProvider());
  }


  @Test
  void getHeaderProvider() {
    assertNotSame(footer, sourceProvider.getHeaderProvider());
    sourceProvider.setHeaderProvider(footer);
    assertSame(footer, sourceProvider.getHeaderProvider());
  }


  @Test
  void getSource() throws StencilNotFoundException, StencilStorageException {
    memory.putFile(Locale.ROOT, "test", "Hello, World!\n");
    assertEquals("Hello, World!\n", sourceProvider.getSource(Source.of("test"), Locale.ROOT));
  }


  @Test
  void getSourceProvider() {
    assertSame(memory, sourceProvider.getSourceProvider());
  }


  @Test
  void getSourceRoot() {
    assertEquals(memory.getSourceRoot(), sourceProvider.getSourceRoot());
  }


  @Test
  void setFooter() throws StencilNotFoundException, StencilStorageException {
    sourceProvider.setFooter("--footer--");
    memory.putFile(Locale.ROOT, "test", "Hello, World!\n");
    assertEquals("Hello, World!\n--footer--", sourceProvider.getSource(Source.of("test"), Locale.ROOT));
  }


  @Test
  void setFooterProvider() throws StencilNotFoundException, StencilStorageException {
    sourceProvider.setFooterProvider(footer);
    memory.putFile(Locale.ROOT, "test", "Hello, World!\n");
    assertEquals("Hello, World!\nFOOT:en_GB", sourceProvider.getSource(Source.of("test"), Locale.UK));
  }


  @Test
  void setHeader() throws StencilNotFoundException, StencilStorageException {
    sourceProvider.setHeader("--header--");
    memory.putFile(Locale.ROOT, "test", "Hello, World!\n");
    assertEquals("--header--Hello, World!\n", sourceProvider.getSource(Source.of("test"), Locale.ROOT));
  }


  @Test
  void setHeaderProvider() throws StencilNotFoundException, StencilStorageException {
    sourceProvider.setHeaderProvider(header);
    memory.putFile(Locale.ROOT, "test", "Hello, World!\n");
    assertEquals("HEAD:en_GB\nHello, World!\n", sourceProvider.getSource(Source.of("test"), Locale.UK));
  }

}