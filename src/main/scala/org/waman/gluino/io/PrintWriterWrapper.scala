package org.waman.gluino.io

import java.io.{Writer, File, Closeable, PrintWriter}
import java.nio.file.{Files, Path}

trait PrintWriterWrapperLike[T <: PrintWriterWrapperLike[T]] { self: T =>

  protected def getPrintWriter: PrintWriter

  def withPrintWriter[R](consumer: PrintWriter => R): R = {
    val pw = getPrintWriter
    try{
      consumer(pw)
    }finally{
      pw.flush()
      pw.close()
    }
  }

  def append(input: Writable): Unit = input.writeTo(getPrintWriter)
  def <<(input: Writable): T = { append(input); this }
}

class PrintWriterWrapper private (private[io] val printWriter: PrintWriter)
  extends PrintWriterWrapperLike[PrintWriterWrapper] with Closeable{

  protected override def getPrintWriter: PrintWriter = printWriter

  override def close(): Unit = {
    printWriter.flush()
    printWriter.close()
  }
}

object PrintWriterWrapper{

  def apply(pw: PrintWriter): PrintWriterWrapper = new PrintWriterWrapper(pw)
  def apply(writer: Writer): PrintWriterWrapper = apply(new PrintWriter(writer))
  def apply(path: Path): PrintWriterWrapper = apply(Files.newBufferedWriter(path))
  def apply(file: File): PrintWriterWrapper = apply(file.toPath)
}
