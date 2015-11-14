package org.waman.gluino.io

import java.io.{Closeable, BufferedWriter, Writer, PrintWriter}

trait WriterWrapperLike[T <: WriterWrapperLike[T]]
  extends GluinoIO with PrintWriterWrapperLike[T]{ self: T =>

  protected def getWriter: BufferedWriter

  override protected def getPrintWriter: PrintWriter = newPrintWriter

  def newPrintWriter: PrintWriter = new PrintWriter(getWriter)

  def withWriter[R](consumer: BufferedWriter => R): R = {
    val writer = getWriter
    try{
      consumer(writer)
    }finally{
      writer.flush()
      writer.close()
    }
  }

  override def append(input: Writable): Unit = input.writeTo(getWriter)
}

class WriterWrapper private (private[io] val writer: BufferedWriter)
    extends WriterWrapperLike[WriterWrapper] with Closeable{

  override protected def getWriter: BufferedWriter = writer

  def writeLine(line: String): Unit = {
    val w = getWriter
    w.write(line)
    w.write(lineSeparator)
  }

  def writeLines(lines: Seq[String]): Unit = {
    val w = getWriter
    lines.foreach{ line =>
      w.write(line)
      w.write(lineSeparator)
    }
  }

  /** flush and close the inner writer. */
  override def close(): Unit = {
    writer.flush()
    writer.close()
  }
}

object WriterWrapper{

  def apply(writer: Writer): WriterWrapper = writer match {
    case bw: BufferedWriter => new WriterWrapper(bw)
    case _ => new WriterWrapper(new BufferedWriter(writer))
  }
}