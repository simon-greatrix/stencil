package com.pippsford.stencil.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.pippsford.stencil.Source;

/**
 * @author Simon Greatrix on 20/01/2021.
 */
class CompendiumSourceProviderTest {

  CompendiumSourceProvider provider = new CompendiumSourceProvider(List.of("compendium_bar.txt", "compendium_foo.txt"));


  @Test
  public void test1() throws StencilStorageException, StencilNotFoundException {
    assertEquals("Hello, World!\n", provider.getSource(Source.of("/hello"), Locale.CHINA));
  }


  @Test
  public void test2() throws StencilStorageException, StencilNotFoundException {
    assertEquals("Bonjour!\n", provider.getSource(Source.of("/hello"), Locale.FRANCE));
  }


  @Test
  public void test3() throws StencilStorageException, StencilNotFoundException {
    assertEquals("How about merging something in?\n", provider.getSource(Source.of("/merge"), Locale.FRANCE));
  }


  @Test
  public void test4() throws StencilStorageException, StencilNotFoundException {
    provider = new CompendiumSourceProvider("compendium_bar.txt");
    assertNull(provider.getSource(Source.of("/unknown/file.txt"), Locale.FRANCE));
  }


  @Test
  public void test5() throws StencilStorageException, StencilNotFoundException {
    assertEquals("\n"
        + "this is something\n"
        + "\n"
        + "which goes on for a bit\n"
        + "\n"
        + "/and/has/paths/in/it\n"
        + "\n"
        + "but that's OK\n"
        + "\n"
        + "provided we see EOT\n", provider.getSource(Source.of("/here/it/is"), Locale.FRANCE));
  }


  @Test
  public void test6() {
    assertThrows(CompendiumMarkerMissingException.class, () -> provider.getSource(Source.of("/test/no/eot"), Locale.FRANCE));
  }


  @Test
  public void test7() throws StencilStorageException, StencilNotFoundException {
    assertEquals("Farwell! Adieu!\n"
        + "\n"
        + "// slash slash //\n", provider.getSource(Source.of("/bye"), Locale.FRANCE));
  }


  @Test
  public void test8() throws StencilStorageException, StencilNotFoundException {
    provider = new CompendiumSourceProvider("compendium_bar.txt");
    assertNull(provider.getSource(Source.of("/and/has/paths/in/it"), Locale.FRANCE));
  }


  @Test
  public void test9() {
    provider = new CompendiumSourceProvider(List.of("compendium_foo.txt", "compendium_bar.txt"));
    assertThrows(CompendiumMarkerMissingException.class, () -> provider.getSource(Source.of("/bye"), Locale.FRANCE));
  }

}