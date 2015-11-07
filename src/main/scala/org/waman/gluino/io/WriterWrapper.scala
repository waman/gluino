package org.waman.gluino.io

import java.io.{BufferedWriter, Writer}

class WriterWrapper private (private[io] val writer: BufferedWriter)
    extends WriterWrapperLike[WriterWrapper]{

  override protected def getWriter: BufferedWriter = writer
}

object WriterWrapper{

  def apply(writer: Writer): WriterWrapper = writer match {
    case bw: BufferedWriter => new WriterWrapper(bw)
    case _ => new WriterWrapper(new BufferedWriter(writer))
  }
}