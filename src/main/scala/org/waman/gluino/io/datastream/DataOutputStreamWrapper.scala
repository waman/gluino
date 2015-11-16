package org.waman.gluino.io.datastream

import java.io._
import java.nio.file.{Files, Path}

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

class DataOutputStreamWrapper private (stream: DataOutputStream)
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

object DataOutputStreamWrapper{

  def apply(dos: DataOutputStream): DataOutputStreamWrapper = new DataOutputStreamWrapper(dos)
  def apply(os: OutputStream): DataOutputStreamWrapper = apply(new DataOutputStream(os))
  def apply(path: Path): DataOutputStreamWrapper = apply(Files.newOutputStream(path))
  def apply(file: File): DataOutputStreamWrapper = apply(file.toPath)
}