package org.waman.gluino.io

import java.io.PrintWriter

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

class PrintWriterWrapper(private[io] val printWriter: PrintWriter)
  extends PrintWriterWrapperLike[PrintWriterWrapper]{

  protected override def getPrintWriter: PrintWriter = printWriter
}
