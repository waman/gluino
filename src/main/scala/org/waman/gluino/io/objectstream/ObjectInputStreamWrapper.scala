package org.waman.gluino.io.objectstream

import java.io._
import java.nio.file.{Files, Path}

import scala.annotation.tailrec

trait ObjectInputStreamWrapperLike{

  protected def getObjectInputStream: ObjectInputStream

  def withObjectInputStream[R](consumer: ObjectInputStream => R): R = {
    val ois = getObjectInputStream

    try{
      consumer(ois)
    }finally{
      ois.close()
    }
  }

  def eachAnyRef(consumer: AnyRef => Unit): Unit = withObjectInputStream{ ois =>
    @tailrec
    def readAnyRef(): Unit = {
      consumer(ois.readObject())
      readAnyRef()
    }

    try{
      readAnyRef()
    }catch{
      case ex: EOFException =>
      case ex: IOException => throw ex
    }
  }

  def readAnyRefs(n: Int): Seq[AnyRef] = withObjectInputStream{ ois =>
    @tailrec
    def readAnyRefRecurse(seq: Seq[AnyRef], i: Int): Seq[AnyRef] = i match {
      case 0 => seq
      case _ => readAnyRefRecurse(seq :+ ois.readObject(), i-1)
    }

    readAnyRefRecurse(Nil, n)
  }
}

class ObjectInputStreamWrapper private (stream: ObjectInputStream)
    extends ObjectInputStreamWrapperLike with Closeable{

  override protected def getObjectInputStream: ObjectInputStream = stream

  override def close(): Unit = stream.close()
}

object ObjectInputStreamWrapper{

  def apply(ois: ObjectInputStream): ObjectInputStreamWrapper = new ObjectInputStreamWrapper(ois)
  def apply(is: InputStream): ObjectInputStreamWrapper = apply(new ObjectInputStream(is))
  def apply(path: Path): ObjectInputStreamWrapper = apply(Files.newInputStream(path))
  def apply(file: File): ObjectInputStreamWrapper = apply(file.toPath)
}
