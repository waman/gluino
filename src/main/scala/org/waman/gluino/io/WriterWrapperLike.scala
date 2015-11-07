package org.waman.gluino.io

import java.io.{BufferedWriter, PrintWriter}

trait WriterWrapperLike[T <: WriterWrapperLike[T]]
    extends GluinoIO with PrintWriterWrapperLike[T]{ self: T =>

  protected def getWriter: BufferedWriter

  override protected def getPrintWriter: PrintWriter = new PrintWriter(getWriter)

  def withWriter(consumer: BufferedWriter => Unit): Unit = {
    val writer = getWriter
    try{
      consumer(writer)
    }finally{
      writer.flush()
      writer.close()
    }
  }

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

  override def append(input: Writable): Unit = input.writeTo(getWriter)
}
