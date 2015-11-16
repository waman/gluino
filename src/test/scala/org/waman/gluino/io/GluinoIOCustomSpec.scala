package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset
import java.nio.file.{Files, Path, StandardOpenOption}

import org.scalatest.matchers.{BeMatcher, MatchResult}
import org.scalatest.{FreeSpec, Matchers}
import org.waman.gluino.FourPhaseInformer
import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}

import scala.collection.JavaConversions._

trait GluinoIOCustomSpec extends FreeSpec with Matchers with FourPhaseInformer{

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

  //***** Encoded File Fixture *****
  // content
  val contentISO2022 = List("1行目", "2行目", "3行目")
  val contentAsStringISO2022 = contentISO2022.map(_ + sep).mkString

  lazy val ISO2022 = Charset.forName("ISO-2022-JP")
  lazy val readOnlyPathISO2022 = createReadOnlyFileISO2022()
  lazy val readOnlyFileISO2022 = readOnlyPathISO2022.toFile

  def createReadOnlyFileISO2022(): Path = {
    val path = Files.createTempFile(null, null)
    Files.write(path, contentISO2022, ISO2022)
    path
  }

  // InputStream, Reader
  trait InputStreamISO2022Fixture{
    val input = Files.newInputStream(readOnlyPathISO2022)
  }

  trait ReaderISO2022Fixture{
    val reader = Files.newBufferedReader(readOnlyPathISO2022)
  }

  //***** Custom Matchers *****
  def opened = BeMatcher{ io: Any =>
    val exec: () => Any = io match {
      case pw: PrintWriter => () => {
        pw.write(GluinoIO.lineSeparator)
        if(pw.checkError())throw new IOException()
      }

      case pww: PrintWriterWrapper => () => {
        pww.printWriter.write(GluinoIO.lineSeparator)
        if(pww.printWriter.checkError())throw new IOException()
      }

      case input : InputStream  => input.available
      case output: OutputStream => () => { output.write(GluinoIO.lineSeparator.getBytes) }
      case reader: Reader => reader.ready
      case writer: Writer => writer.flush

      case isw: InputStreamWrapper => isw.stream.available
      case osw: OutputStreamWrapper => () => { osw.stream.write(GluinoIO.lineSeparator.getBytes) }
      case rw: ReaderWrapper => rw.reader.ready
      case ww: WriterWrapper => ww.writer.flush
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