package org.waman.gluino.io

import java.io.{BufferedReader, Reader}

class ReaderWrapper private (val reader: BufferedReader) extends ReaderWrapperLike

object ReaderWrapper{

  def apply(reader: Reader): ReaderWrapper = reader match {
    case br: BufferedReader => new ReaderWrapper(br)
    case _ => new ReaderWrapper(new BufferedReader(reader))
  }
}