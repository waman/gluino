package org.waman.gluino.io

import java.io.{Closeable, OutputStream}

class OutputStreamWrapper(private[io] val stream: OutputStream)
    extends OutputStreamWrapperLike[OutputStreamWrapper] with Closeable{

  override def close(): Unit = stream.close()

  override protected def getOutputStream: OutputStream = stream
}