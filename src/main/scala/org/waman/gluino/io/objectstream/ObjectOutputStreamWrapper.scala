package org.waman.gluino.io.objectstream

import java.io._
import java.nio.file.{Files, Path}

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

class ObjectOutputStreamWrapper private (stream: ObjectOutputStream)
    extends ObjectOutputStreamWrapperLike
    with ObjectOutputExtension[ObjectOutputStreamWrapper]{

  override protected def getObjectOutputStream: ObjectOutputStream = stream
  override protected def getObjectOutput: ObjectOutput = stream
}

object ObjectOutputStreamWrapper{

  def apply(oos: ObjectOutputStream): ObjectOutputStreamWrapper = new ObjectOutputStreamWrapper(oos)
  def apply(os: OutputStream): ObjectOutputStreamWrapper = apply(new ObjectOutputStream(os))
  def apply(path: Path): ObjectOutputStreamWrapper = apply(Files.newOutputStream(path))
  def apply(file: File): ObjectOutputStreamWrapper = apply(file.toPath)

}
