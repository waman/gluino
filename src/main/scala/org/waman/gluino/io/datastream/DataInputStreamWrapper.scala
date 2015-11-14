package org.waman.gluino.io.datastream

import java.io.DataInputStream

trait DataInputStreamWrapperLike {

  protected def getDataInputStream: DataInputStream

  def withDataInputStream[R](consumer: DataInputStream => R): R = {
    val dis = getDataInputStream

    try consumer(dis)
    finally dis.close()
  }
}

class DataInputStreamWrapper(stream: DataInputStream) extends DataInputStreamWrapperLike{

  override protected def getDataInputStream: DataInputStream = stream
}
