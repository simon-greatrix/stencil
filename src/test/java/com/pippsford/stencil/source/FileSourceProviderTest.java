package com.pippsford.stencil.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.pippsford.stencil.SourceProvider;
import com.pippsford.stencil.StencilException;
import com.pippsford.stencil.Stencils;

/**
 * @author Simon Greatrix on 07/01/2021.
 */
class FileSourceProviderTest {

  static FileSystem fileSystem;

  static Path rootPath;


  @BeforeAll
  static void setup() throws IOException {
    fileSystem = Jimfs.newFileSystem(Configuration.windows());
    rootPath = fileSystem.getPath("C:\\setl");
    Files.createDirectories(rootPath);
    Files.createDirectories(rootPath.resolve("test"));
    Files.writeString(rootPath.resolve("test/copyright.txt"), "(C) Copyright SETL");
    Files.writeString(rootPath.resolve("test/file1.txt"), "Hello {title} {name}!\n"
        + "[include copyright.txt]");
    Files.writeString(rootPath.resolve("test/index"), "This is an index.");
    Files.writeString(rootPath.resolve("test/index.txt"), "There are some files in this folder.");
  }


  @Test
  public void coverage() {
    assertNotNull(new FileSourceProvider(rootPath).toString());
  }


  @Test
  public void test1() throws StencilException {
    SourceProvider provider = new FileSourceProvider(rootPath.resolve("test"));
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Frobisher"
    );
    String output = stencils.write("file1.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Mr Frobisher!\n(C) Copyright SETL", output);
  }


  @Test
  public void test2() {
    SourceProvider provider = new FileSourceProvider(rootPath);
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Frobisher"
    );
    assertThrows(StencilNotFoundException.class, () -> stencils.write("unknown.txt", Locale.UK, ZoneId.of("Europe/London"), map));
  }


  @Test
  public void test3() throws StencilException {
    SourceProvider provider = new FileSourceProvider(rootPath);
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Duckworth"
    );
    String output = stencils.write("test/file1.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Mr Duckworth!\n(C) Copyright SETL", output);
  }


  @Test
  public void test4() throws StencilException {
    SourceProvider provider = new FileSourceProvider(rootPath);
    Stencils stencils = new Stencils(provider);
    Map<String, Object> map = Map.of(
        "title", "Mr",
        "name", "Hamster"
    );
    String output = stencils.write("test/file1.txt", Locale.UK, ZoneId.of("Europe/London"), map);
    assertEquals("Hello Mr Hamster!\n(C) Copyright SETL", output);
  }

}