package org.apache.cassandra.gui.gui.util;

import java.nio.ByteBuffer;


public final class ByteBufferHelper {

  public static ByteBuffer toByteBuffer(byte[] obj) {
    if (obj == null) {
      return null;
    }
    return ByteBuffer.wrap(obj);
  }

  public static byte[] fromByteBuffer(ByteBuffer byteBuffer) {
    if (byteBuffer == null) {
      return null;
    }
    byte[] bytes = new byte[byteBuffer.remaining()];
    byteBuffer.get(bytes, 0, bytes.length);
    return bytes;
  }
}