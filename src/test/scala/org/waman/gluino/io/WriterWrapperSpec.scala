package org.waman.gluino.io

import java.io.{BufferedWriter, Writer}
import java.nio.file.{Files, Path, StandardOpenOption}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}

trait WriterWrapperLikeSpec[T <: WriterWrapperLike[T]]
  extends PrintWriterWrapperLikeSpec[T] with AppendableConverter{

  protected def newWriterWrapperLike(path: Path): T

  override protected def newPrintWriterWrapperLike(path: Path) = newWriterWrapperLike(path)

  private trait SUT extends DestFileFixture{
    val sut = newWriterWrapperLike(destPath)
  }

  private trait SUTWithContent extends DestFileWithContentFixture{
    val sut = newWriterWrapperLike(destPath)
  }

  "withWriter() method should" - {

    "close the writer after use" in new SUT {
      __Exercise__
      val result = sut.withWriter{ w => w }
      __Verify__
      result should be (closed)
    }

    "close the writer when exception thrown" in new SUT {
      __Exercise__
      var result: Writer = null
      try{
        sut.withWriter{ w =>
          result = w
          throw new RuntimeException()
        }
      }catch{
        case ex: RuntimeException =>
      }
      __Verify__
      result should be (closed)
    }

    "be able to use with the loan pattern" in new SUT{
      __Exercise__
      sut.withWriter { w =>
        w.write(contentAsString)
      }
      __Verify__
      text(destPath) should equal (contentAsString)
    }
  }

  // WriterWrapper#append() is not implicitly applied due to overloading
  "append(Writable) method should append the specified lines to the writer" in new SUTWithContent{
    __Exercise__
    sut.append("fourth line.")
    closeIfCloseable(sut)
    __Verify__
    text(destPath) should equal (contentAsString + "fourth line.")
  }

  "<< operator for Writable (in WriterWrapper) should" - {

    "append the specified Writable to the writer" in new SUTWithContent {
      __Exercise__
      sut << "fourth line."
      closeIfCloseable(sut)
      __Verify__
      text(destPath) should equal (contentAsString + "fourth line.")
    }

    "sequentially append the specified Writables to the writer" in new SUTWithContent {
      __Exercise__
      sut << "fourth " << "line." << sep
      closeIfCloseable(sut)
      __Verify__
      text(destPath) should equal (contentAsString + "fourth line." + sep)
    }
  }
}

trait CloseableWriterWrapperLikeSpec[T <: WriterWrapperLike[T]]
    extends WriterWrapperLikeSpec[T]
    with CloseablePrintWriterWrapperLikeSpec[T]

class WriterWrapperSpec
    extends CloseableWriterWrapperLikeSpec[WriterWrapper]
    with MockFactory{

  override protected def newWriterWrapperLike(path: Path): WriterWrapper =
    WriterWrapper(Files.newBufferedWriter(path, StandardOpenOption.APPEND))

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

  private trait SUT extends DestFileWithContentFixture{
    val sut = newWriterWrapperLike(destPath)
  }

  "writeLine(String) method should" - {

    "NOT close the writer" in new SUT{
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

    "NOT close the writer" in new SUT{
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