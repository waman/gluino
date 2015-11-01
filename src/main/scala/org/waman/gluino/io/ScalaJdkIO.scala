package org.waman.gluino.io

import scala.language.implicitConversions
import java.io._
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets._

import scala.util.matching.Regex

trait ScalaJdkIO[P <: ScalaJdkIO[P]] extends GluinoIO{ p: P =>

  implicit def convertStringToCharset(charset: String): Charset = Charset.forName(charset)

  //***** Byte array, Byte stream *****
  // bytes
  def bytes: Array[Byte]
  def bytes_=(bytes: Array[Byte]): Unit

  def eachByte(consumer: Byte => Unit): Unit = newInputStream().eachByte(consumer)

  // new Input/Output stream
  def newInputStream(): InputStream
  def newOutputStream(append: Boolean = false): OutputStream

  // "with" methods
  def withInputStream(consumer: InputStream => Unit): Unit =
    newInputStream().withInputStream(consumer)

  def withOutputStream(consumer: OutputStream => Unit): Unit =
    newOutputStream().withOutputStream(consumer)

  def withOutputStreamAppend(consumer: OutputStream => Unit): Unit =
    newOutputStream(true).withOutputStream(consumer)

  // append, <<
  def append(input: Outputtable): Unit = withOutputStreamAppend(input.outputTo)
  def <<(input: Outputtable): P = { append(input); this }

  //***** String, Reader/Writer *****
  // text (String)
  def text: String = text(UTF_8)
  def text(charset: Charset): String = newReader(charset).text

  def text_=(text: String): Unit = setText(text)
  def setText(text: String, charset: Charset = UTF_8) = newWriter(charset).withWriter(_.write(text))

  // new Reader/Writer/PrintWriter
  def newReader(charset: Charset = UTF_8): BufferedReader

  def newWriter(charset: Charset = UTF_8, append: Boolean = false): BufferedWriter

  def newPrintWriter(charset: Charset = UTF_8): PrintWriter =
    new PrintWriter(newWriter(charset, append = true))

  // "with" methods
  def withReader(consumer: BufferedReader => Unit): Unit = withReader(UTF_8)(consumer)
  def withReader(charset: Charset = UTF_8)(consumer: BufferedReader => Unit): Unit =
    newReader(charset).withReader(consumer)

  def withWriter(consumer: BufferedWriter => Unit): Unit = withWriter(UTF_8)(consumer)
  def withWriter(charset: Charset)(consumer: BufferedWriter => Unit): Unit =
    newWriter(charset).withWriter(consumer)

  def withWriterAppend(consumer: BufferedWriter => Unit): Unit = withWriterAppend(UTF_8)(consumer)
  def withWriterAppend(charset: Charset)(consumer: BufferedWriter => Unit): Unit =
    newWriter(charset, append = true).withWriter(consumer)

  def withPrintWriter(consumer: PrintWriter => Unit): Unit = withPrintWriter(UTF_8)(consumer)

  /** open APPEND and do not close */
  def withPrintWriter(charset: Charset)(consumer: PrintWriter => Unit): Unit =
    newPrintWriter(charset).withPrintWriter(consumer)

  // append, <<
  def append(input: Writable): Unit = withWriterAppend(input.writeTo(_))
  def <<(input: Writable): P = { append(input); this }


  //***** Lines *****
  def readLines(charset: Charset = UTF_8): Seq[String] = newReader(charset).readLines()
  def writeLines(lines: Seq[String], charset: Charset = UTF_8, append: Boolean = false): Unit =
    newWriter(charset, append).writeLines(lines)

  def eachLine(consumer: String => Unit): Unit = eachLine(UTF_8)(consumer)
  def eachLine(charset: Charset)(consumer: String => Unit): Unit =
    newReader(charset).eachLine(consumer)

  def eachLine(consumer: (String, Int) => Unit): Unit = eachLine(UTF_8, 0)(consumer)
  def eachLine(charset: Charset, n0: Int = 0)(consumer: (String, Int) => Unit) =
    newReader(charset).eachLine(n0)(consumer)

  def splitEachLine(regex: Regex, charset: Charset = UTF_8)(consumer: List[String] => Unit): Unit =
    newReader(charset).splitEachLine(regex)(consumer)

  def filterLine(filter: String => Boolean): Writable = filterLine(UTF_8)(filter)
  def filterLine(charset: Charset)(filter: String => Boolean): Writable =
    newReader(charset).filterLine(filter)

  def filterLine(writer: Writer)(filter: String => Boolean): Unit = filterLine(writer, UTF_8)(filter)
  def filterLine(writer: Writer, charset: Charset)(filter: String => Boolean): Unit =
    newReader(charset).filterLine(writer)(filter)
}
