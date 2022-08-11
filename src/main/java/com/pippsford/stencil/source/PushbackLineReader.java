package com.pippsford.stencil.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;

/**
 * Line Reader for reading from Compendium files. Allows an "unread" operation to make parsing simpler.
 *
 * @author Simon Greatrix on 13/02/2021.
 */
class PushbackLineReader {

  private final URI inputSource;

  private final BufferedReader reader;

  private String line = null;


  PushbackLineReader(URI inputSource, BufferedReader reader) {
    this.inputSource = inputSource;
    this.reader = reader;
  }


  String readLine() throws StencilStorageException {
    if (line != null) {
      String l = line;
      line = null;
      return l;
    }
    try {
      return reader.readLine();
    } catch (IOException ioException) {
      throw new StencilStorageException("Unable to read from storage for: " + inputSource.toASCIIString(), ioException);
    }
  }


  void unread(String oldLine) {
    if (line != null) {
      throw new IllegalStateException("Already have unread line");
    }
    line = oldLine;
  }

}
