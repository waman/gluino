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

  def withOutputStreamAppend(consumer: OutputStream => Unit): Unit =
    newOutputStream(true).withOutputStream(consumer)

  def bytes_=(bytes: Array[Byte]): Unit

  //***** String, Reader/Writer *****
  def newReader(charset: Charset): BufferedReader
  def newWriter(charset: Charset, append: Boolean): BufferedWriter

  override protected def getReader: BufferedReader = newReader(defaultCharset)
  override protected def getWriter: BufferedWriter = newWriter(defaultCharset, append = false)

  def withWriterAppend(consumer: BufferedWriter => Unit): Unit =
    withWriterAppend(defaultCharset)(consumer)

  def withWriterAppend(charset: Charset)(consumer: BufferedWriter => Unit): Unit = {
    val writer = newWriter(charset, append = false)
    try{
      consumer(writer)
    }finally{
      writer.flush()
      writer.close()
    }
  }

  def text_=(text: String): Unit = setText(text)
  def setText(text: String, charset: Charset = defaultCharset) =
    newWriter(charset).withWriter(_.write(text))

  override def append(input: Writable): Unit = input.writeTo(newWriter(defaultCharset, append = true))

  //***** PrintWriter *****
  def newPrintWriter(charset: Charset, append: Boolean): PrintWriter =
    new PrintWriter(newWriter(charset, append))

  def withPrintWriterAppend(charset: Charset)(consumer: PrintWriter => Unit): Unit =
    newPrintWriter(charset, append = true).withPrintWriter(consumer)
}
