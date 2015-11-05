package org.waman.gluino.io

import java.io.{InputStreamReader, InputStream, BufferedReader, Writer}
import java.nio.charset.{StandardCharsets, Charset}

import scala.annotation.tailrec
import scala.util.matching.Regex

trait InputStreamWrapperLike extends ReaderWrapperLike{

  val implicitCharset: Charset = StandardCharsets.UTF_8
  protected def inputStream: InputStream

  //***** withInputStream *****
  def withInputStream(consumer: InputStream => Unit): Unit = try{
    consumer(inputStream)
  }finally{
    inputStream.close()
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

  //***** Reader factory/accessor *****
  protected def newReaderWrapper(charset: Charset): ReaderWrapper =
    ReaderWrapper(createBufferedReader(charset))

  override protected def reader: BufferedReader = createBufferedReader(implicitCharset)

  private def createBufferedReader(charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(inputStream, charset))

  //***** ReaderWrapperLike method with Charset *****
  //***** withReader *****
  def withReader(charset: Charset)(consumer: (BufferedReader) => Unit): Unit =
    newReaderWrapper(charset).withReader(consumer)

  //***** text (String) *****
  def eachChar(charset: Charset)(consumer: (Char) => Unit): Unit =
    newReaderWrapper(charset).eachChar(consumer)

  def text(charset: Charset): String = newReaderWrapper(charset).text

  //***** lines *****
  def eachLine(charset: Charset)(consumer: (String) => Unit): Unit =
    newReaderWrapper(charset).eachLine(consumer)

  def eachLine(n0: Int, charset: Charset)(consumer: (String, Int) => Unit): Unit =
    newReaderWrapper(charset).eachLine(n0)(consumer)

  def splitEachLine(regex: Regex, charset: Charset)(consumer: (List[String]) => Unit): Unit =
    newReaderWrapper(charset).splitEachLine(regex)(consumer)

  def filterLine(charset: Charset)(filter: (String) => Boolean): Writable =
    newReaderWrapper(charset).filterLine(filter)

  def filterLine(writer: Writer, charset: Charset)(filter: (String) => Boolean): Unit =
    newReaderWrapper(charset).filterLine(writer)(filter)

  def readLines(charset: Charset): Seq[String] = newReaderWrapper(charset).readLines()
}
