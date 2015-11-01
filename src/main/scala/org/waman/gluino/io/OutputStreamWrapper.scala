package org.waman.gluino.io

import java.io.OutputStream

class OutputStreamWrapper(private[io] val outputStream: OutputStream){

  def withOutputStream(consumer: OutputStream => Unit): Unit = try{
    consumer(outputStream)
  }finally{
    outputStream.flush()
    outputStream.close()
  }
}