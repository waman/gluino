package org.waman.gluino.io

import java.io.PrintWriter

trait PrintWriterWrapperLike[T <: PrintWriterWrapperLike[T]] { self: T =>

  protected def getPrintWriter: PrintWriter

  /** do not close stream */
  def withPrintWriter(consumer: PrintWriter => Unit): Unit = {
    val pw = getPrintWriter
    try{
      consumer(pw)
    }finally{
      pw.flush()
    }
  }

  def append(input: Writable): Unit = input.writeTo(getPrintWriter)
  def <<(input: Writable): T = { append(input); this }
}
