package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset
import java.nio.file.{Files, Path, StandardOpenOption}

import org.scalatest.enablers.{Size, Readability, Writability, Existence}
import org.scalatest.matchers.{BeMatcher, MatchResult}
import org.scalatest.{FreeSpec, Matchers}
import org.waman.gluino.FourPhaseInformer
import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}
import org.waman.gluino.io.datastream.{DataOutputStreamWrapper, DataInputStreamWrapper}
import org.waman.gluino.io.objectstream.{ObjectInputStreamWrapper, ObjectOutputStreamWrapper}
import org.waman.gluino.nio.GluinoPath

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
    val path = GluinoPath.createTempFile()
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
    val destPath = GluinoPath.createTempFile()
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
    val path = GluinoPath.createTempFile()
    Files.write(path, contentISO2022, ISO2022)
    path
  }

  trait DestFileWithContentISO2022Fixture extends DestFileFixture{
    Files.write(destPath, contentISO2022, ISO2022)
  }

  //***** Data Stream *****
  lazy val readOnlyPathData = createReadOnlyPathData()
  lazy val readOnlyFileData = readOnlyPathData.toFile

  def createReadOnlyPathData(): Path = {
    val path = GluinoPath.createTempFile()
    val oos = new DataOutputStream(Files.newOutputStream(path))
    try{
      oos.writeInt(1)
      oos.writeLong(2L)
      oos.writeDouble(3.0d)
      oos.writeUTF("UTF")
      oos.writeBytes("string")

      oos.flush()
    }finally oos.close()

    path
  }

  trait DataInputStreamFixture{
    val input = Files.newInputStream(readOnlyPathData)
    val ois = new DataInputStream(input)
  }

  trait DestFileWithDataFixture{
    val destPath = createReadOnlyPathData()
    lazy val destFile = destPath.toFile
  }

  //***** Object Stream *****
  // content
  val contentObjects = List("1", new Integer(2), BigDecimal(3))

  lazy val readOnlyPathObjects = createReadOnlyPathObjects()
  lazy val readOnlyFileObjects = readOnlyPathObjects.toFile

  def createReadOnlyPathObjects(): Path = {
    val path = GluinoPath.createTempFile()
    val oos = new ObjectOutputStream(Files.newOutputStream(path))
    try{
      contentObjects.foreach(oos.writeObject(_))
      oos.flush()
    }finally oos.close()

    path
  }

  trait ObjectInputStreamFixture{
    val input = Files.newInputStream(readOnlyPathObjects)
    val ois = new ObjectInputStream(input)
  }

  trait DestFileWithObjectsFixture{
    val destPath = createReadOnlyPathObjects()
    lazy val destFile = destPath.toFile
  }

  //***** Custom Matchers *****
  def opened = BeMatcher{ io: Any =>
    val exec: () => Any = io match {
      case pw: PrintWriter => () => {
        pw.write("a")
        if(pw.checkError())throw new IOException()
      }

      case pww: PrintWriterWrapper => () => {
        pww.printWriter.write("a")
        if(pww.printWriter.checkError())throw new IOException()
      }

      case ois: ObjectInputStream => () => { ois.readObject() }
      case oos: ObjectOutputStream => () => { oos.writeObject("a") }

      case input : InputStream  => input.available
      case output: OutputStream => () => { output.write("a".getBytes) }
      case reader: Reader => reader.ready
      case writer: Writer => writer.flush

      case isw: InputStreamWrapper => isw.stream.available
      case dis: DataInputStreamWrapper => dis.stream.available
      case osw: OutputStreamWrapper => () => { osw.stream.write("a".getBytes) }
      case dos: DataOutputStreamWrapper => () => { dos.stream.write("a".getBytes) }
      case ois: ObjectInputStreamWrapper => () => { ois.stream.readObject() }
      case oos: ObjectOutputStreamWrapper => () => { oos.stream.writeObject("a") }
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

  implicit object PathEnabler extends Existence[Path]
      with Readability[Path] with Writability[Path] with Size[Path]{

    override def exists(path: Path): Boolean = Files.exists(path)

    override def isReadable(path: Path): Boolean = Files.isReadable(path)

    override def isWritable(path: Path): Boolean = Files.isWritable(path)

    override def sizeOf(path: Path): Long = Files.size(path)
  }
}