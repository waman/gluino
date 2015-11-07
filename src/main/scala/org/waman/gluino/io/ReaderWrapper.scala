package org.waman.gluino.io

import java.io.{BufferedReader, Reader}

class ReaderWrapper private (private[io] val reader: BufferedReader) extends ReaderWrapperLike{

  override protected def getReader: BufferedReader = reader
}

object ReaderWrapper{

  def apply(reader: Reader): ReaderWrapper = reader match {
    case br: BufferedReader => new ReaderWrapper(br)
    case _ => new ReaderWrapper(new BufferedReader(reader))
  }
}