package org.waman.gluino.io

import java.io.{Closeable, InputStream}

class InputStreamWrapper(protected[io] val stream: InputStream)
    extends InputStreamWrapperLike with Closeable{

  override protected def getInputStream: InputStream = stream

  override def close(): Unit = stream.close()
}
