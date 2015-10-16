package org.waman.gluino.io

import java.io.PrintWriter

class PrintWriterWrapper(writer: PrintWriter){

  /** do not close stream */
  def withPrintWriter(consumer: PrintWriter => Unit): Unit =
    try{
      consumer(writer)
    }finally{
      writer.flush()
    }
}
