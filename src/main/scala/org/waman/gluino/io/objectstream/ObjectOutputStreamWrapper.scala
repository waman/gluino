package org.waman.gluino.io.objectstream

import java.io.{ObjectOutput, ObjectOutputStream}

trait ObjectOutputStreamWrapperLike{

  protected def getObjectOutputStream: ObjectOutputStream

  def withObjectOutputStream[R](consumer: ObjectOutputStream => R): R = {
    val oos = getObjectOutputStream

    try{
      consumer(oos)
    }finally{
      oos.flush()
      oos.close()
    }
  }
}

class ObjectOutputStreamWrapper(stream: ObjectOutputStream)
    extends ObjectOutputStreamWrapperLike
    with ObjectOutputExtension[ObjectOutputStreamWrapper]{

  override protected def getObjectOutputStream: ObjectOutputStream = stream
  override protected def getObjectOutput: ObjectOutput = stream
}
