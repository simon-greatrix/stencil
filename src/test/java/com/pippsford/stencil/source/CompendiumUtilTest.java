package com.pippsford.stencil.source;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.Test;

/**
 * @author Simon Greatrix on 20/01/2021.
 */
class CompendiumUtilTest {

  @Test
  public void test1() throws Exception {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
    Path path = fileSystem.getPath("/test.txt");
    Files.writeString(path, "/xyz\nfoo\n/abc\nbar\n");

    CompendiumUtil.main(new String[]{"sort", path.toUri().toString()});
    String out = Files.readString(path);
    assertEquals("/abc\n"
        + "bar\n"
        + "\n"
        + "\n"
        + "\n"
        + "/xyz\n"
        + "foo\n", out);
  }


  @Test
  public void test2() throws Exception {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
    Path path = fileSystem.getPath("/test.txt");
    Files.writeString(path, "/xyz >> S\nfoo\n/a/b/c\nS\n/abc\nbar\n");

    CompendiumUtil.main(new String[]{"sort", path.toUri().toString()});
    String out = Files.readString(path);
    assertEquals("/abc\n"
        + "bar\n"
        + "\n"
        + "\n"
        + "\n"
        + "/xyz >> EOT\n"
        + "foo\n"
        + "/a/b/c\n"
        + "EOT\n", out);
  }


  @Test
  public void test3() throws Exception {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
    Path path = fileSystem.getPath("/test.txt");
    Files.writeString(path, "/xyz >> S\nfoo\n/a/b/c\nEOT\nS\n/abc\nbar\n");

    CompendiumUtil.main(new String[]{"sort", path.toUri().toString()});
    String out = Files.readString(path);
    assertEquals("/abc\n"
        + "bar\n"
        + "\n"
        + "\n"
        + "\n"
        + "/xyz >> §§§\n"
        + "foo\n"
        + "/a/b/c\n"
        + "EOT\n"
        + "§§§\n", out);
  }


  @Test
  public void test4() throws Exception {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
    Path path = fileSystem.getPath("/test.txt");
    Files.writeString(path, "/xyz >> S\nfoo\n/a/b/c\nEOT\n§§§\nS\n/abc\nbar\n");

    CompendiumUtil.main(new String[]{"sort", path.toUri().toString()});
    String out = Files.readString(path);
    assertEquals("/abc\n"
        + "bar\n"
        + "\n"
        + "\n"
        + "\n"
        + "/xyz >> §§§:0015:5921\n"
        + "foo\n"
        + "/a/b/c\n"
        + "EOT\n"
        + "§§§\n"
        + "§§§:0015:5921\n", out);

    CompendiumUtil.main(new String[]{"sort", path.toUri().toString()});
    out = Files.readString(path);
    assertEquals("/abc\n"
        + "bar\n"
        + "\n"
        + "\n"
        + "\n"
        + "/xyz >> §§§:0015:5921\n"
        + "foo\n"
        + "/a/b/c\n"
        + "EOT\n"
        + "§§§\n"
        + "§§§:0015:5921\n", out);
  }


  @Test
  public void test5() {
    String text = CompendiumUtil.writeCompendium(Map.of("/abc", "Salt:00129b\nEOT\n§§§\n§§§:0030:0000\n\n"));
    assertTrue(text.endsWith("fbl\n"));
  }

}