package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import org.waman.gluino.io.objectstream.ObjectInputStreamWrapperLike

import scala.annotation.tailrec
import scala.util.matching.Regex

import org.apache.commons.io.input.ClassLoaderObjectInputStream
import org.waman.gluino.io.datastream.DataInputStreamWrapperLike

trait InputStreamWrapperLike extends GluinoIO
    with ObjectInputStreamWrapperLike
    with DataInputStreamWrapperLike
    with ReaderWrapperLike{

  protected def getInputStream: InputStream

  //***** withInputStream *****
  def withInputStream[R](consumer: InputStream => R): R = {
    val is = getInputStream
    try{
      consumer(is)
    }finally{
      is.close()
    }
  }

  //***** Byte *****
  def eachByte(consumer: Byte => Unit): Unit = withInputStream{ input =>
    @tailrec
    def consumeByte(byte: Int): Unit = byte match {
      case -1 =>
      case _ =>
        consumer(byte.asInstanceOf[Byte])
        consumeByte(input.read())
    }

    consumeByte(input.read())
  }

  def bytes: Array[Byte] = withInputStream{ is =>
    @tailrec
    def readByteRecurse(bytes: Seq[Byte]): Seq[Byte] = is.read() match {
      case -1 => bytes
      case b  => readByteRecurse(bytes :+ b.asInstanceOf[Byte])
    }

    readByteRecurse(Nil).toArray
  }

  //***** ObjectInputStream, DataInputStream *****
  override protected def getObjectInputStream: ObjectInputStream = newObjectInputStream()

  def newObjectInputStream(): ObjectInputStream = new ObjectInputStream(getInputStream)
  def newObjectInputStream(classLoader: ClassLoader): ObjectInputStream =
    new ClassLoaderObjectInputStream(classLoader, getInputStream)

  def withObjectInputStream[R](classLoader: ClassLoader)(consumer: ObjectInputStream => R): R =
    newObjectInputStream(classLoader).withObjectInputStream(consumer)


  override protected def getDataInputStream: DataInputStream = newDataInputStream()
  def newDataInputStream(): DataInputStream = new DataInputStream(getInputStream)

  //***** Reader factory/accessor *****
  override protected def getReader: BufferedReader = newReader(defaultCharset)

  def newReader(charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(getInputStream, charset))

  //+++++ ReaderWrapperLike method with Charset +++++
  //***** withReader *****
  def withReader[R](charset: Charset)(consumer: (BufferedReader) => R): R =
    newReader(charset).withReader(consumer)

  //***** text (String) *****
  def eachChar(charset: Charset)(consumer: (Char) => Unit): Unit =
    newReader(charset).eachChar(consumer)

  def transformChar(writer: Writer, charset: Charset)(map: Char => Char): Unit =
    newReader(charset).transformChar(writer)(map)

  override def text: String = super.text
  def text(charset: Charset): String = newReader(charset).text

  //***** lines *****
  def eachLine(charset: Charset)(consumer: (String) => Unit): Unit =
    newReader(charset).eachLine(consumer)

  def eachLine(n0: Int, charset: Charset)(consumer: (String, Int) => Unit): Unit =
    newReader(charset).eachLine(n0)(consumer)

  def splitEachLine(regex: Regex, charset: Charset)(consumer: (List[String]) => Unit): Unit =
    newReader(charset).splitEachLine(regex)(consumer)

  def filterLine(charset: Charset)(filter: (String) => Boolean): Writable =
    newReader(charset).filterLine(filter)

  def filterLine(writer: Writer, charset: Charset)(filter: (String) => Boolean): Unit =
    newReader(charset).filterLine(writer)(filter)

  def transformLine(writer: Writer, charset: Charset)(map: String => String): Unit =
    newReader(charset).transformLine(writer)(map)

  override def readLines(): Seq[String] = super.readLines()
  def readLines(charset: Charset): Seq[String] = newReader(charset).readLines()
}

class InputStreamWrapper private (protected[io] val stream: InputStream)
    extends InputStreamWrapperLike with Closeable{

  override protected def getInputStream: InputStream = stream

  override def close(): Unit = stream.close()
}

object InputStreamWrapper{

  def apply(stream: InputStream): InputStreamWrapper = new InputStreamWrapper(stream)
  def apply(path: Path): InputStreamWrapper = apply(Files.newInputStream(path))
  def apply(file: File): InputStreamWrapper = apply(file.toPath)

}