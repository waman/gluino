package org.waman.gluino.io

import java.io.{Closeable, BufferedWriter, Writer}

class WriterWrapper private (private[io] val writer: BufferedWriter)
    extends WriterWrapperLike[WriterWrapper] with Closeable{

  override protected def getWriter: BufferedWriter = writer

  override def close(): Unit = writer.close()
}

object WriterWrapper{

  def apply(writer: Writer): WriterWrapper = writer match {
    case bw: BufferedWriter => new WriterWrapper(bw)
    case _ => new WriterWrapper(new BufferedWriter(writer))
  }
}