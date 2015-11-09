package org.waman.gluino.io

import java.io.{InputStream, ObjectInputStream}

class ObjectInputStreamWrapper(stream: ObjectInputStream)
    extends InputStreamWrapperLike{

  protected def getObjectInputStream: ObjectInputStream = stream
  override protected def getInputStream: InputStream = stream

  def eachObject(classLoader: ClassLoader)(consumer: Any => Unit): Unit = ???

  def eachObject(consumer: Any => Unit): Unit = ???
}
