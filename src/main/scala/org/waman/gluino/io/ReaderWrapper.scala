package org.waman.gluino.io

import java.io.{Closeable, BufferedReader, Reader}

class ReaderWrapper private (private[io] val reader: BufferedReader)
    extends ReaderWrapperLike with Closeable{

  override protected def getReader: BufferedReader = reader

  override def close(): Unit = reader.close()
}

object ReaderWrapper{

  def apply(reader: Reader): ReaderWrapper = reader match {
    case br: BufferedReader => new ReaderWrapper(br)
    case _ => new ReaderWrapper(new BufferedReader(reader))
  }
}