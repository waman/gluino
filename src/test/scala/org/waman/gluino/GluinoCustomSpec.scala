package org.waman.gluino

import java.io.{Writer, Reader, IOException}
import java.nio.file.{Path, Files}

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.{MatchResult, BeMatcher}
import org.scalatest.{FreeSpec, Matchers}
import org.waman.gluino.io.GluinoIO
import scala.collection.JavaConversions._

class GluinoCustomSpec extends FreeSpec with Matchers with MockFactory with FourPhaseInformer{

  trait FileFixture{
    val path = Files.createTempFile(null, null)
    val file = path.toFile
  }

  val content = List("first line.", "second line.", "third line.")
  val contentAsString = content.mkString(GluinoIO.lineSep)

  lazy val readOnlyPath = createReadOnlyFile()
  lazy val readOnlyFile = readOnlyPath.toFile

  def createReadOnlyFile(): Path = {
    val path = Files.createTempFile(null, null)
    Files.write(path, content)

    if(path.getFileSystem.supportedFileAttributeViews() contains "acl"){
      null // TODO
    }
    
    path
  }
  
  trait FileWithContentFixture extends FileFixture{
    Files.write(path, content)
  }

  trait InputStreamFixture{
    val input = Files.newInputStream(readOnlyPath)
  }

  trait outputStreamFixture extends FileFixture{
    val output = Files.newOutputStream(path)
  }

  trait outputStreamWithContentFixture extends FileWithContentFixture{
    val output = Files.newOutputStream(path)
  }

  trait ReaderFixture{
    val reader = Files.newBufferedReader(readOnlyPath)
  }

  trait WriterFixture extends FileFixture{
    val writer = Files.newBufferedReader(path)
  }

  trait WriterWithContentFixture extends FileWithContentFixture{
    val writer = Files.newBufferedReader(path)
  }

  def closed = BeMatcher{ io: Any =>
    val exec: () => Any = io match {
      case reader: Reader => reader.ready
      case writer: Writer => writer.flush
    }
    val ex = the [IOException] thrownBy { exec() }
    MatchResult(
      ex.getMessage == "Stream closed",
      "Stream is not closed",
      "Stream is closed")
  }
}