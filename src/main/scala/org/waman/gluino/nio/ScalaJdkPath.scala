package org.waman.gluino.nio

import java.io._
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{StandardOpenOption, Files, Path}

import scala.collection.JavaConversions._

class ScalaJdkPath(path: Path) {

  //***** Byte array, Byte stream *****
  def bytes: Array[Byte] = Files.readAllBytes(path)

  def foreachByte(consumer: Byte => Unit): Unit = withInputStream{ input =>
    var b = input.read()
    while(b != -1){
      consumer(b.asInstanceOf[Byte])
      b = input.read()
    }
  }

  def bytes_=(bytes: Array[Byte]): Unit = Files.write(path, bytes)

  def withInputStream(consumer: InputStream => Unit): Unit = {
    val input = Files.newInputStream(path)
    try consumer(input) finally input.close()
  }

  def withOutputStream(consumer: OutputStream => Unit): Unit = {
    val output = Files.newOutputStream(path)
    try consumer(output) finally{
      output.flush()
      output.close()
    }
  }

  def withOutputStreamAppend(consumer: OutputStream => Unit): Unit = {
    val output = Files.newOutputStream(path, StandardOpenOption.APPEND)
    try consumer(output) finally{
      output.flush()
      output.close()
    }
  }

  def append(input: Outputtable): Unit = {
    withOutputStreamAppend{ output =>
      input.outputTo(output)
    }
  }

  def <<(input: Outputtable): ScalaJdkPath = { append(input); this }

  //***** String, Reader/Writer *****
  val lineSeparator: String = System.getProperty("line.sep")

  def text: String = Files.readAllLines(path, UTF_8).mkString(lineSeparator)
  def text(charset: Charset): String = Files.readAllLines(path, charset).mkString(lineSeparator)

  def foreachLine(charset: Charset = UTF_8)(consumer: String => Unit): Unit = withReader(charset){ reader =>
    var line = reader.readLine()
    while(line != null){
      consumer(line)
      line = reader.readLine()
    }
  }

  def text_=(text: String): Unit = Files.write(path, List(text))

  def withReader(charset: Charset = UTF_8)(consumer: BufferedReader => Unit): Unit = {
    val reader = Files.newBufferedReader(path, UTF_8)
    try consumer(reader) finally reader.close()
  }

  def withWriter(consumer: BufferedWriter => Unit): Unit = withWriter(UTF_8)(consumer)

  def withWriter(charset: Charset)(consumer: BufferedWriter => Unit): Unit = {
    val writer = Files.newBufferedWriter(path, charset)
    try consumer(writer) finally{
      writer.flush()
      writer.close()
    }
  }

  def withWriterAppend(consumer: BufferedWriter => Unit): Unit = withWriterAppend(UTF_8)(consumer)

  def withWriterAppend(charset: Charset)(consumer: BufferedWriter => Unit): Unit = {
    val writer = Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND)
    try consumer(writer) finally{
      writer.flush()
      writer.close()
    }
  }

  def withPrintWriter(consumer: PrintWriter => Unit): Unit = withPrintWriter(UTF_8)(consumer)

  /** open APPEND and do not close */
  def withPrintWriter(charset: Charset)(consumer: PrintWriter => Unit): Unit = {
    val writer = new PrintWriter(Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND))
    try consumer(writer) finally writer.flush()
  }

  def append(input: Writable): Unit = {
    withWriterAppend{ writer =>
      input.writeTo(writer)
    }
  }

  def <<(input: Writable): ScalaJdkPath = { append(input); this }
}