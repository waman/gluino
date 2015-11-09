package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.matching.Regex

trait InputStreamWrapperLike extends GluinoIO with ReaderWrapperLike{

  protected def getInputStream: InputStream

  //***** withInputStream *****
  def withInputStream(consumer: InputStream => Unit): Unit = {
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

  def bytes: Array[Byte] = {
    var bytes = mutable.MutableList[Byte]()
    eachByte(bytes += _)
    bytes.toArray
  }

  //***** ObjectInputStream, DataInputStream *****
  def newObjectInputStream: ObjectInputStream = new ObjectInputStream(getInputStream)

  private def getObjectInputStream: ObjectInputStream = {
    val is = getInputStream
    is match {
      case ObjectInputStream => is
      case _ => newObjectInputStream
    }
    new ObjectInputStream(getInputStream)
  }

  def newObjectInputStream(classLoader: ClassLoader): ObjectInputStream = ???

  def withObjectInputStream(consumer: ObjectInputStream => Unit): Unit =
    withObjectInputStream(getObjectInputStream, consumer)

  def withObjectInputStream(classLoader: ClassLoader)(consumer: ObjectInputStream => Unit): Unit =
    withObjectInputStream(newObjectInputStream(classLoader), consumer)

  private def withObjectInputStream(ois: ObjectInputStream, consumer: ObjectInputStream => Unit): Unit =
    try{
      consumer(ois)
    }finally{
      ois.close()
    }

  def newDataInputStream: DataInputStream = new DataInputStream(getInputStream)

  def withDataInputStream(consumer: DataInputStream => Unit): Unit = {
    val is = getInputStream
    val dis = is match{
      case DataInputStream => is.asInstanceOf[DataInputStream]
      case _ => new DataInputStream(is)
    }
    try{
      consumer(dis)
    }finally{
      dis.close()
    }
  }

  //***** Reader factory/accessor *****
  override protected def getReader: BufferedReader = newReader(defaultCharset)

  def newReader(charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(getInputStream, charset))

  //+++++ ReaderWrapperLike method with Charset +++++
  //***** withReader *****
  def withReader(charset: Charset)(consumer: (BufferedReader) => Unit): Unit =
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

  override def readLines: Seq[String] = super.readLines
  def readLines(charset: Charset): Seq[String] = newReader(charset).readLines
}
