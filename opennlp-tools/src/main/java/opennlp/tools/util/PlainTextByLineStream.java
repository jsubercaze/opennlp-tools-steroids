/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package opennlp.tools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Reads a plain text file and return each line as a <code>String</code> object.
 */
public class PlainTextByLineStream implements ObjectStream<String> {

  private final FileChannel channel;
  private final String encoding;

  private InputStreamFactory inputStreamFactory;

  private BufferedReader in;

  public PlainTextByLineStream(InputStreamFactory inputStreamFactory, String charsetName) throws IOException {
    this(inputStreamFactory, Charset.forName(charsetName));
  }

  public PlainTextByLineStream(InputStreamFactory inputStreamFactory, Charset charset) throws IOException {
    this.inputStreamFactory = inputStreamFactory;
    this.channel = null;
    this.encoding = charset.name();

    reset();
  }

  /**
   * Initializes the current instance.
   *
   * @param in
   * @deprecated Use {@link #PlainTextByLineStream(InputStreamFactory, Charset)} instead.
   */
  public PlainTextByLineStream(Reader in) {
    this.in = new BufferedReader(in);
    this.channel = null;
    this.encoding = null;
  }

  /**
   * @deprecated Use {@link #PlainTextByLineStream(InputStreamFactory, String)} instead.
   */
  public PlainTextByLineStream(InputStream in, String charsetName) throws UnsupportedEncodingException {
    this(new InputStreamReader(in, charsetName));
  }

  /**
   * @deprecated Use {@link #PlainTextByLineStream(InputStreamFactory, Charset)} instead.
   */
  public PlainTextByLineStream(InputStream in, Charset charset) {
    this(new InputStreamReader(in, charset));
  }

  /**
   * @deprecated Use {@link #PlainTextByLineStream(InputStreamFactory, String)} instead.
   */
  public PlainTextByLineStream(FileChannel channel, String charsetName) {
    this.encoding = charsetName;
    this.channel = channel;

    // TODO: Why isn't reset called here ?
    in = new BufferedReader(Channels.newReader(channel, encoding));
  }

  /**
   * @deprecated Use {@link #PlainTextByLineStream(InputStreamFactory, Charset)} instead.
   */
  public PlainTextByLineStream(FileChannel channel, Charset encoding) {
    this(channel, encoding.name());
  }

  public String read() throws IOException {
    return in.readLine();
  }

  public void reset() throws IOException {

    if (inputStreamFactory != null) {
      in = new BufferedReader(new InputStreamReader(inputStreamFactory.createInputStream(), encoding));
    }
    else if (channel == null) {
        in.reset();
    }
    else {
      channel.position(0);
      in = new BufferedReader(Channels.newReader(channel, encoding));
    }
  }

  public void close() throws IOException {

      if (in != null && channel == null) {
        in.close();
      }
      else if (channel != null) {
       channel.close();
      }
  }
}
