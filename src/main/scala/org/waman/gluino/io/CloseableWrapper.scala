package org.waman.gluino.io

import java.io.Closeable

class CloseableWrapper(closeable: Closeable) extends Closeable{

  def withCloseable[R](consumer: Closeable => R): R =
    try consumer(closeable)
    finally closeable.close()

  override def close(): Unit = closeable.close()
}
