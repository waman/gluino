package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset
import java.nio.file.{Files, Path, StandardOpenOption}

import org.scalatest.BeforeAndAfterAll
import org.scalatest.enablers.{Existence, Readability, Size, Writability}
import org.scalatest.matchers.{BeMatcher, MatchResult}
import org.waman.gluino.GluinoCustomSpec
import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}
import org.waman.gluino.io.datastream.{DataInputStreamWrapper, DataOutputStreamWrapper}
import org.waman.gluino.io.objectstream.{ObjectInputStreamWrapper, ObjectOutputStreamWrapper}
import org.waman.gluino.nio.{DirectoryBuilder, PathWrapper, GluinoPath}

import scala.collection.JavaConversions._

import scala.collection.mutable

trait GluinoIOCustomSpec extends GluinoCustomSpec with BeforeAndAfterAll{

  protected def createNotExistingFile(): Path = {
    val path = GluinoPath.createTempFile(deleteOnExit = true)
    Files.delete(path)
    path
  }

  protected def createNotExistingDirectory(): Path = {
    val path = GluinoPath.createTempDirectory(deleteOnExit = true)
    Files.delete(path)
    path
  }

  protected def text(path: Path): String = text(path, GluinoIO.defaultCharset)
  protected def text(path: Path, charset: Charset): String = new String(Files.readAllBytes(path), charset)

  //***** Fixtures *****
  // content
  val content = List("first line.", "second line.", "third line.")
  val contentAsString = content.map(_ + sep).mkString

  lazy val readOnlyPath = createReadOnlyFile()
  lazy val readOnlyFile = readOnlyPath.toFile

  def createReadOnlyFile(): Path = {
    val path = GluinoPath.createTempFile(deleteOnExit = true)
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
  trait FileFixture{
    val path = GluinoPath.createTempFile(deleteOnExit = true)
  }

  trait OutputStreamFixture extends FileFixture{
    val output = Files.newOutputStream(path)
  }

  trait WriterFixture extends FileFixture{
    val writer = Files.newBufferedWriter(path)
  }

  // OutputStream, Writer with content
  trait FileWithContentFixture extends FileFixture{
    Files.write(path, content)
  }

  trait OutputStreamWithContentFixture extends FileWithContentFixture{
    val output = Files.newOutputStream(path, StandardOpenOption.APPEND)
  }

  trait WriterWithContentFixture extends FileWithContentFixture{
    val writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)
  }

  //***** Encoded File Fixture *****
  // content
  val contentISO2022 = List("1行目", "2行目", "3行目")
  val contentAsStringISO2022 = contentISO2022.map(_ + sep).mkString

  lazy val ISO2022 = Charset.forName("ISO-2022-JP")
  lazy val readOnlyPathISO2022 = createReadOnlyFileISO2022()
  lazy val readOnlyFileISO2022 = readOnlyPathISO2022.toFile

  def createReadOnlyFileISO2022(): Path = {
    val path = GluinoPath.createTempFile(deleteOnExit = true)
    Files.write(path, contentISO2022, ISO2022)
    path
  }

  trait FileWithContentISO2022Fixture extends FileFixture{
    Files.write(path, contentISO2022, ISO2022)
  }

  //**** Directory Fixture *****
  trait DirectoryFixture{
    val dir = GluinoPath.createTempDirectory(deleteOnExit = true)
  }

  trait NotEmptyDirectoryFixture extends DirectoryFixture{
    val childPath = GluinoPath.createTempFile(dir, deleteOnExit = true)
  }

  private val deleteOnExitDirs = new mutable.MutableList[Path]()
  val readOnlyDir: Path = initDirectory()
  override def afterAll{
    deleteOnExitDirs.foreach(new PathWrapper(_).deleteDir())
  }

  protected def initDirectory
      (parent: Path = GluinoPath.createTempDirectory(prefix = "123-", deleteOnExit = true)): Path = {
    val dir = new DirectoryBuilder{
      val baseDir = parent
      file("child1.txt")
      file("child2.txt")
      file("child3.txt")
      dir("dir1"){
        file("child11.txt")
        file("child12.txt")
      }
      dir("dir2"){
        file("child21.txt")
        file("child22.txt")
        file("child23.txt")
      }
      dir("dir3"){
        file("child31.txt")
        dir("dir31"){
          file("child311.txt")
          file("child312.txt")
        }
      }
    }.baseDir
    deleteOnExitDirs += dir
    dir
  }

  trait DirectoryWithFilesFixture extends DirectoryFixture{
    initDirectory(dir)
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