package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

import scala.language.implicitConversions

trait FileWrapperLike[T <: FileWrapperLike[T]] extends GluinoIO
    with InputStreamWrapperLike with OutputStreamWrapperLike[T]
    with ReaderWrapperLike with WriterWrapperLike[T] with PrintWriterWrapperLike[T]{ self: T =>

  //***** Byte, InputStream/OutputStream *****
  def newInputStream: InputStream
  def newOutputStream(append: Boolean = false): OutputStream

  override protected def getInputStream: InputStream = newInputStream
  override protected def getOutputStream: OutputStream = newOutputStream(false)

  def withOutputStreamAppend[R](consumer: OutputStream => R): R =
    newOutputStream(true).withOutputStream(consumer)

  def bytes_=(bytes: Array[Byte]): Unit

  //***** String, Reader/Writer *****
  def newReader(charset: Charset): BufferedReader
  def newWriter(charset: Charset, append: Boolean): BufferedWriter

  override protected def getReader: BufferedReader = newReader(defaultCharset)
  override protected def getWriter: BufferedWriter = newWriter(defaultCharset, append = false)

  def withWriterAppend[R](consumer: BufferedWriter => R): R =
    withWriterAppend(defaultCharset)(consumer)

  def withWriterAppend[R](charset: Charset)(consumer: BufferedWriter => R): R = {
    val writer = newWriter(charset, append = false)
    try{
      consumer(writer)
    }finally{
      writer.flush()
      writer.close()
    }
  }

  override def text: String = super.text
  override def text(charset: Charset): String = super.text(charset)
  def text_=(text: String): Unit = setText(text, defaultCharset)
  def setText(text: String, charset: Charset) =
    newWriter(charset).withWriter(_.write(text))

  override def readLines: Seq[String] = super.readLines
  override def readLines(charset: Charset): Seq[String] = super.readLines(charset)

  override def append(input: Writable): Unit =
    newWriter(defaultCharset, append = true).withWriter(_.append(input))

  //***** PrintWriter *****
  def newPrintWriter(charset: Charset, append: Boolean): PrintWriter =
    new PrintWriter(newWriter(charset, append))

  def withPrintWriterAppend[R](charset: Charset)(consumer: PrintWriter => R): R =
    newPrintWriter(charset, append = true).withPrintWriter(consumer)
}
