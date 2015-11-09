package org.waman.gluino.io

import java.io.Closeable

class CloseableWrapper(closeable: Closeable){

  def withCloseable(consumer: Closeable => Unit): Unit =
    try{
      consumer(closeable)
    }finally{
      closeable.close()
    }
}
