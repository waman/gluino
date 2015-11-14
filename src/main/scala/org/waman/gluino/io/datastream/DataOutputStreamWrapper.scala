package org.waman.gluino.io.datastream

import java.io.{DataOutput, DataOutputStream, Closeable}

trait DataOutputStreamWrapperLike{

  protected def getDataOutputStream: DataOutputStream

  def withDataOutputStream[R](consumer: DataOutputStream => R): R = {
    val dos = getDataOutputStream

    try{
      consumer(dos)
    }finally{
      dos.flush()
      dos.close()
    }
  }
}

class DataOutputStreamWrapper(stream: DataOutputStream)
    extends DataOutputStreamWrapperLike
    with DataOutputExtension[DataOutputStreamWrapper]
    with Closeable{

  override protected def getDataOutputStream: DataOutputStream = stream
  override protected def getDataOutput: DataOutput = stream

  override def close(): Unit = {
    stream.flush()
    stream.close()
  }
}
