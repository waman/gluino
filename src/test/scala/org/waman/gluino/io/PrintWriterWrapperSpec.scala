package org.waman.gluino.io

import java.io.{Closeable, PrintWriter}
import java.nio.file.{Files, Path, StandardOpenOption}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}

trait PrintWriterWrapperLikeSpec[T <: PrintWriterWrapperLike[T]]
    extends GluinoIOCustomSpec with AppendableConverter{

  protected def newPrintWriterWrapperLike(path: Path): T

  private trait SUT extends FileFixture{
    val sut = newPrintWriterWrapperLike(path)
  }

  private trait SUTWithContent extends FileWithContentFixture{
    val sut = newPrintWriterWrapperLike(path)
  }

  "withPrintWriter() method should" - {

    "close the PrintWriter after use" in new SUT {
      __Exercise__
      val result = sut.withPrintWriter{ pw => pw }
      __Verify__
      result should be (closed)
    }

    "close the PrintWriter when exception thrown" in new SUT {
      __Exercise__
      var result: PrintWriter = null
      try{
        sut.withPrintWriter{ pw =>
          result = pw
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
      sut.withPrintWriter { pw =>
        pw.write(contentAsString)
      }
      __Verify__
      text(path) should equal (contentAsString)
    }
  }

  // PrintWriterWrapper#append() is not implicitly applied due to overloading
  "append(Writable) method should append the specified lines to the PrintWriter" in new SUTWithContent{
    __Exercise__
    sut.append("fourth line.")
    closeIfCloseable(sut)
    __Verify__
    text(path) should equal (contentAsString + "fourth line.")
  }

  "<< operator for Writable (in PrintWriterWrapper) should" - {

    "append the specified Writable to the PrintWriter" in new SUTWithContent {
      __Exercise__
      sut << "fourth line."
      closeIfCloseable(sut)
      __Verify__
      text(path) should equal (contentAsString + "fourth line.")
    }

    "sequentially append the specified Writables to the PrintWriter" in new SUTWithContent {
      __Exercise__
      sut << "fourth " << "line." << sep
      closeIfCloseable(sut)
      __Verify__
      text(path) should equal (contentAsString + "fourth line." + sep)
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

  trait PrintWriterWrapperLikeFixture extends FileFixture{
    val sut = newPrintWriterWrapperLike(path)
  }

  "Some methods of PrintWriterWrapperLike trait should NOT close the PrintWriter after use" - {

    /** WriterWrapper#append() is not implicitly applied due to overloading */
    "WriterWrapper#append() method" in new PrintWriterWrapperLikeFixture{
      __Exercise__
      sut.append("first line.")
      __Verify__
      sut should be (opened)
    }

    "<< operator" in new PrintWriterWrapperLikeFixture{
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
}
