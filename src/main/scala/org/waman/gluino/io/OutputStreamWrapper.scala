package org.waman.gluino.io

import java.io.OutputStream

class OutputStreamWrapper(output: OutputStream){

  def withOutputStream(consumer: OutputStream => Unit): Unit = try{
    consumer(output)
  }finally{
    output.flush()
    output.close()
  }
}
