package org.waman.gluino.io

import java.io.{OutputStream, ObjectOutputStream}

class ObjectOutputStreamWrapper(stream: ObjectOutputStream)
    extends OutputStreamWrapperLike[ObjectOutputStreamWrapper]{

  protected def getObjectOutputStream: ObjectOutputStream = stream
  override protected def getOutputStream: OutputStream = stream

  def <<(value: AnyRef): Unit = {
    getObjectOutputStream.writeObject(value)
  }
}
