package org.waman.gluino.io

import java.io.{BufferedWriter, Writer}
import java.nio.file.{Files, Path, StandardOpenOption}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}
import org.waman.gluino.nio.GluinoPath

import scala.collection.JavaConversions._

trait WriterWrapperLikeSpec[T <: WriterWrapperLike[T]]
  extends PrintWriterWrapperLikeSpec[T] with AppendableConverter{

  protected def newWriterWrapperLike(path: Path): T

  override protected def newPrintWriterWrapperLike(path: Path) = newWriterWrapperLike(path)
}

trait CloseableWriterWrapperLikeSpec[T <: WriterWrapperLike[T]]
    extends WriterWrapperLikeSpec[T]
    with CloseablePrintWriterWrapperLikeSpec[T]

class WriterWrapperSpec
    extends CloseableWriterWrapperLikeSpec[WriterWrapper]
    with MockFactory{

  override protected def newWriterWrapperLike(path: Path): WriterWrapper =
    WriterWrapper(Files.newBufferedWriter(path, StandardOpenOption.APPEND))

  private trait SUT {
    val destPath = GluinoPath.createTempFile()
    Files.write(destPath, content)

    val sut = newWriterWrapperLike(destPath)
  }

  "***** Factory method *****" - {

    "the Writer is retained directly by wrapper if an instance of BufferedWriter" in {
      __SetUp__
      val writer = new BufferedWriter(mock[Writer])
      __Exercise__
      val wrapper = WriterWrapper(writer)
      __Verify__
      wrapper.writer should be (a [BufferedWriter])
      wrapper.writer should be theSameInstanceAs writer
    }

    "the Writer is wrapped by BufferedWriter and then retained if not an instance of BufferedWriter" in {
      __SetUp__
      val writer = mock[Writer]
      __Exercise__
      val wrapper = WriterWrapper(writer)
      __Verify__
      wrapper.writer should be (a [BufferedWriter])
      wrapper.writer should not be theSameInstanceAs (writer)
    }
  }

  private trait MockedWriterWrapper{
    val writer = mock[Writer]
    inSequence {
      (writer.flush _).expects().anyNumberOfTimes()
      (writer.close _).expects()
    }
    val sut = WriterWrapper(writer)
  }

  "withWriter() method should" - {

    "flush and close the writer after use" in new MockedWriterWrapper {
      __Verify__
      sut.withWriter { _ => }
    }

    "close the writer when exception thrown" in new MockedWriterWrapper {
      __Verify__
      try{
        sut.withWriter{ _ => throw new RuntimeException() }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }

  "withPrintWriter() method should" - {

    "flush and close writer after use" in new MockedWriterWrapper {
      __Verify__
      sut.withPrintWriter { _ => }
    }

    "close the writer when exception thrown" in new MockedWriterWrapper {
      __Verify__
      try{
        sut.withPrintWriter{ _ => throw new RuntimeException() }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }

  "writeLine(String) method should" - {

    "not close the writer" in new SUT{
      __Exercise__
      sut.writeLine("first line.")
      __Verify__
      sut should be (opened)
    }

    "write down the specified line to the writer" in new SUT{
      __Exercise__
      sut.writeLine("fourth line.")
      sut.close()
      __Verify__
      text(destPath) should equal (contentAsString + "fourth line." + sep)
    }
  }

  "writeLines(Seq[String]) method should" - {

    "not close the writer" in new SUT{
      __Exercise__
      sut.writeLines(Seq("first line.", "second line."))
      __Verify__
      sut should be (opened)
    }

    "write down the specified lines to the writer" in new SUT {
      __Exercise__
      sut.writeLines(Seq("fourth line.", "fifth line."))
      sut.close()
      __Verify__
      text(destPath) should equal (contentAsString + "fourth line." + sep + "fifth line." + sep)
    }
  }
}