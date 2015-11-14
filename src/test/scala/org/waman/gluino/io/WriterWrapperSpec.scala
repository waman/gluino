package org.waman.gluino.io

import java.io.{BufferedWriter, Closeable, Writer}
import java.nio.file.{Files, Path, StandardOpenOption}

import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}

import scala.collection.JavaConversions._

trait WriterWrapperLikeSpec[T <: WriterWrapperLike[T]]
  extends GluinoIOCustomSpec with AppendableConverter{

  def newWriterWrapperLike(dest: Path): T

  private trait SUT {
    val destPath = Files.createTempFile(null, null)
    Files.write(destPath, content)

    val sut = newWriterWrapperLike(destPath)
  }

  // WriterWrapper#append() is not implicitly applied due to overloading
  "WriterWrapper#append() method should append the specified lines to the writer" in new SUT{
    __Exercise__
    sut.append("fourth line.")
    closeIfCloseable(sut)
    __Verify__
    text(destPath) should equal (contentAsString + "fourth line.")
  }

  "<< operator should" - {

    "append the specified Writable to the writer" in new SUT {
      __Exercise__
      sut << "fourth line."
      closeIfCloseable(sut)
      __Verify__
      text(destPath) should contain theSameElementsInOrderAs
        (contentAsString + "fourth line.")
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

  def text(path: Path): String = new String(Files.readAllBytes(path))
}

trait CloseableWriterWrapperLikeSpec[T <: WriterWrapperLike[T]]
  extends WriterWrapperLikeSpec[T]{

  private trait SUT{
    val destPath = Files.createTempFile(null, null)
    val sut = newWriterWrapperLike(destPath)
  }

  "Some methods of WriterWrapperLike trait should properly close reader after use" - {

    "withWriter() method" in {
      __SetUp__
      val writer = mock[Writer]
      (writer.flush _).expects()
      (writer.close _).expects()
      __Exercise__
      writer.withWriter{ _ => }
      __Verify__
    }
  }

  "Some methods of WriterWrapperLike trait should not close reader after use" - {

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

class WriterWrapperSpec extends WriterWrapperLikeSpec[WriterWrapper]{

  override def newWriterWrapperLike(dest: Path): WriterWrapper =
    WriterWrapper(Files.newBufferedWriter(dest, StandardOpenOption.APPEND))

  private trait SUT {
    val destPath = Files.createTempFile(null, null)
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

  "writeLine(String) method should" - {

    "write down the specified line to the writer" in new SUT{
      __Exercise__
      sut.writeLine("fourth line.")
      closeIfCloseable(sut)
      __Verify__
      text(destPath) should equal (contentAsString + "fourth line." + sep)
    }

    "close the Writer after use" in new SUT{
      __Exercise__
      sut.writeLine("first line.")
      __Verify__
      sut should be (opened)
    }
  }

  "writeLines(Seq[String]) method should" - {

    "write down the specified lines to the writer" in new SUT {
      __Exercise__
      sut.writeLines(Seq("fourth line.", "fifth line."))
      closeIfCloseable(sut)
      __Verify__
      text(destPath) should equal (contentAsString + "fourth line." + sep + "fifth line." + sep)
    }

    "close the Writer after use" in new SUT{
      __Exercise__
      sut.writeLines(Seq("first line.", "second line."))
      __Verify__
      sut should be (opened)
    }

  }
}