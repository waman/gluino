package org.waman.gluino.io.objectstream

import java.io.{ObjectOutput, DataOutput, Closeable}

import org.waman.gluino.io.datastream.DataOutputExtension

trait ObjectOutputExtension[T <: ObjectOutputExtension[T]]
    extends DataOutputExtension[T]
    with Closeable{ self: T =>

  protected def getObjectOutput: ObjectOutput
  override protected def getDataOutput: DataOutput = getObjectOutput

  def <<(value: AnyRef): T = {
    getObjectOutput.writeObject(value)
    this
  }

  override def close(): Unit = getObjectOutput.close()
}
