package org.waman.gluino

import java.io._
import java.nio.file.{StandardOpenOption, Files, Path}

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.{BeMatcher, MatchResult}
import org.scalatest.{FreeSpec, Matchers}
import org.waman.gluino.io.GluinoIO
import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}

import scala.collection.JavaConversions._

class GluinoIOCustomSpec extends FreeSpec with Matchers with MockFactory with FourPhaseInformer{

  //***** Utility methods *****
  def convertImplicitly[T](t: T) = t

  //***** Fixtures *****
  // content
  val content = List("first line.", "second line.", "third line.")
  val contentAsString = content.map(_ + sep).mkString

  lazy val readOnlyPath = createReadOnlyFile()
  lazy val readOnlyFile = readOnlyPath.toFile

  def createReadOnlyFile(): Path = {
    val path = Files.createTempFile(null, null)
    Files.write(path, content)
    path
  }

  // InputStream, Reader
  trait InputStreamFixture{
    val input = Files.newInputStream(readOnlyPath)
  }

  trait ReaderFixture{
    val reader = Files.newBufferedReader(readOnlyPath)
  }

  // OutputStream, Writer
  trait DestFileFixture{
    val destPath = Files.createTempFile(null, null)
    val destFile = destPath.toFile
  }

  trait OutputStreamFixture extends DestFileFixture{
    val output = Files.newOutputStream(destPath)
  }

  trait WriterFixture extends DestFileFixture{
    val writer = Files.newBufferedWriter(destPath)
  }

  // OutputStream, Writer with content
  trait DestFileWithContentFixture extends DestFileFixture{
    Files.write(destPath, content)
  }

  trait OutputStreamWithContentFixture extends DestFileWithContentFixture{
    val output = Files.newOutputStream(destPath, StandardOpenOption.APPEND)
  }

  trait WriterWithContentFixture extends DestFileWithContentFixture{
    val writer = Files.newBufferedWriter(destPath, StandardOpenOption.APPEND)
  }

  // InputStream/OutputStream, Reader/Writer
  trait IOStreamFixture extends InputStreamFixture with OutputStreamFixture
  trait IOStreamWithContentFixture extends InputStreamFixture with OutputStreamWithContentFixture
  trait ReaderWriterFixture extends ReaderFixture with WriterFixture
  trait ReaderWriterWithContentFixture extends ReaderFixture with WriterWithContentFixture

  //***** Custom Matchers *****
  def opened = BeMatcher{ io: Any =>
    val exec: () => Any = io match {
      case input : InputStream  => input.available
      case output: OutputStream => () => { output.write(GluinoIO.lineSeparator.getBytes) }
      case reader: Reader => reader.ready
      case writer: Writer => writer.flush
    }

    val isOpened = try {
      exec()
      true
    }catch{
      case ex: IOException => false
    }
    MatchResult(isOpened,
      "Stream closed",
      "Stream opened")
  }

  def closed = not(opened)
}