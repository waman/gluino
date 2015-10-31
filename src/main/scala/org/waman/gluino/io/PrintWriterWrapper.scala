package org.waman.gluino.io

import java.io.PrintWriter

class PrintWriterWrapper(private[io] val writer: PrintWriter){

  /** do not close stream */
  def withPrintWriter(consumer: PrintWriter => Unit): Unit =
    try{
      consumer(writer)
    }finally{
      writer.flush()
    }
}
