package org.waman.gluino.nio

import java.io._
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Files, Path, StandardOpenOption}

import org.waman.gluino.io.ScalaJdkIO

import scala.collection.JavaConversions._

class ScalaJdkPath(path: Path) extends ScalaJdkIO[ScalaJdkPath]{

  //***** Byte array, Byte stream *****
  // bytes
  def bytes: Array[Byte] = Files.readAllBytes(path)
  def bytes_=(bytes: Array[Byte]): Unit = Files.write(path, bytes)

  // new Input/Output stream
  def newInputStream() = Files.newInputStream(path)
  def newOutputStream(append: Boolean = false) =
    if(append)Files.newOutputStream(path, StandardOpenOption.APPEND)
    else Files.newOutputStream(path)

  //***** String, Reader/Writer *****
  // text (String)
  override def setText(text: String, charset: Charset = UTF_8) = Files.write(path, List(text), charset)

  // new Reader/Writer/PrintWriter
  def newReader(charset: Charset = UTF_8): BufferedReader = Files.newBufferedReader(path, charset)

  def newWriter(charset: Charset = UTF_8, append: Boolean = false): BufferedWriter =
    if(append)Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND)
    else Files.newBufferedWriter(path, charset)

  //***** Lines *****
  override def readLines(charset: Charset = UTF_8): Seq[String] = Files.readAllLines(path, charset)
}