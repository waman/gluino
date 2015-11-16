package org.waman.gluino.io

import java.io.{Closeable, PrintWriter, Writer}
import java.nio.file.{StandardOpenOption, Files, Path}

import org.waman.gluino.nio.GluinoPath

import scala.collection.JavaConversions._

import org.scalamock.scalatest.MockFactory

import GluinoIO.{lineSeparator => sep}

trait PrintWriterWrapperLikeSpec[T <: PrintWriterWrapperLike[T]]
    extends GluinoIOCustomSpec with AppendableConverter{

  protected def newPrintWriterWrapperLike(path: Path): T

  private trait SUT {
    val destPath = GluinoPath.createTempFile()
    Files.write(destPath, content)

    val sut = newPrintWriterWrapperLike(destPath)
  }

  // PrintWriterWrapper#append() is not implicitly applied due to overloading
  "append(Writable) method should append the specified lines to the PrintWriter" in new SUT{
    __Exercise__
    sut.append("fourth line.")
    closeIfCloseable(sut)
    __Verify__
    text(destPath) should equal (contentAsString + "fourth line.")
  }

  "<< operator for Writable should" - {

    "append the specified Writable to the writer" in new SUT {
      __Exercise__
      sut << "fourth line."
      closeIfCloseable(sut)
      __Verify__
      text(destPath) should equal (contentAsString + "fourth line.")
    }

    "sequentially append the specified Writables to the writer" in new SUT {
      __Exercise__
      sut << "fourth " << "line." << sep
      closeIfCloseable(sut)
      __Verify__
      text(destPath) should equal (contentAsString + "fourth line." + sep)
    }
  }

  def closeIfCloseable(any: Any): Unit = any match {
    case c: Closeable => c.close()
    case _ =>
  }

  def text(path: Path): String = new String(Files.readAllBytes(path), defaultCharset)
}

trait CloseablePrintWriterWrapperLikeSpec[T <: PrintWriterWrapperLike[T]]
    extends PrintWriterWrapperLikeSpec[T]
    with MockFactory{

  private trait SUT{
    val destPath = GluinoPath.createTempFile()
    val sut = newPrintWriterWrapperLike(destPath)
  }

  "Some methods of PrintWriterWrapperLike trait should not close reader after use" - {

    /** WriterWrapper#append() is not implicitly applied due to overloading */
    "WriterWrapper#append() method" in new SUT{
      __Exercise__
      sut.append("first line.")
      __Verify__
      sut should be (opened)
    }

    "<< operator" in new SUT{
      __Exercise__
      sut << "first line."
      __Verify__
      sut should be (opened)
    }
  }
}

class PrintWriterWrapperSpec
    extends CloseablePrintWriterWrapperLikeSpec[PrintWriterWrapper]{

  override protected def newPrintWriterWrapperLike(path: Path) =
    PrintWriterWrapper(Files.newBufferedWriter(path, StandardOpenOption.APPEND))

  "withPrintWriter() method should" - {

    "flush and close writer after use" in {
      __SetUp__
      val writer = mock[Writer]
      inSequence {
        (writer.flush _).expects().anyNumberOfTimes()
        (writer.close _).expects()
      }
      val sut = PrintWriterWrapper(writer)
      __Verify__
      sut.withPrintWriter { _ => }
    }

    "flush and close writer when exception thrown" in {
      __SetUp__
      val writer = mock[Writer]
      inSequence {
        (writer.flush _).expects().anyNumberOfTimes()
        (writer.close _).expects()
      }
      val sut = PrintWriterWrapper(writer)
      __Verify__
      try {
        sut.withPrintWriter { _ => throw new RuntimeException }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }
}
