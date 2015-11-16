package org.waman.gluino.io.datastream

import java.io.{DataInputStream, File, InputStream}
import java.nio.file.{Files, Path}

trait DataInputStreamWrapperLike {

  protected def getDataInputStream: DataInputStream

  def withDataInputStream[R](consumer: DataInputStream => R): R = {
    val dis = getDataInputStream

    try consumer(dis)
    finally dis.close()
  }
}

class DataInputStreamWrapper private (stream: DataInputStream) extends DataInputStreamWrapperLike{

  override protected def getDataInputStream: DataInputStream = stream
}

object DataInputStreamWrapper{

  def apply(dis: DataInputStream): DataInputStreamWrapper = new DataInputStreamWrapper(dis)
  def apply(is: InputStream): DataInputStreamWrapper = apply(new DataInputStream(is))
  def apply(path: Path): DataInputStreamWrapper = apply(Files.newInputStream(path))
  def apply(file: File): DataInputStreamWrapper = apply(file.toPath)
}
