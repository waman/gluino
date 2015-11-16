package org.waman.gluino.io

import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}
import org.waman.gluino.io.datastream.{CloseableDataOutputStreamWrapperLikeSpec, DataOutputStreamWrapperLikeSpec}
import org.waman.gluino.io.objectstream.{CloseableObjectOutputStreamWrapperLikeSpec, ObjectOutputStreamWrapperLikeSpec}
import org.waman.gluino.nio.GluinoPath

import scala.collection.JavaConversions._

trait OutputStreamWrapperLikeSpec[T <: OutputStreamWrapperLike[T]]
    extends WriterWrapperLikeSpec[T]
    with ObjectOutputStreamWrapperLikeSpec
    with DataOutputStreamWrapperLikeSpec{

  protected def newOutputStreamWrapperLike(path: Path): T

  override protected def newWriterWrapperLike(path: Path) = newOutputStreamWrapperLike(path)
  override protected def newObjectOutputStreamWrapperLike(path: Path) = newOutputStreamWrapperLike(path)
  override protected def newDataOutputStreamWrapperLike(path: Path) = newOutputStreamWrapperLike(path)

  private trait SUT {
    val destPath = GluinoPath.createTempFile()
    Files.write(destPath, contentISO2022, ISO2022)

    val sut = newWriterWrapperLike(destPath)
  }

  // OutputStreamWrapper#append() is not implicitly applied due to overloading
  "append(Outputtable) method should append the specified bytes to the stream" in new SUT{
    __Exercise__
    sut.append("4行目".getBytes(ISO2022))
    closeIfCloseable(sut)
    __Verify__
    text(destPath, ISO2022) should equal (contentAsStringISO2022 + "4行目")
  }

  "<< operator for Outputtable should" - {

    "append the specified Outputtable to the stream" in new SUT {
      __Exercise__
      sut << "4行目".getBytes(ISO2022)
      closeIfCloseable(sut)
      __Verify__
      text(destPath, ISO2022) should equal(contentAsStringISO2022 + "4行目")
    }

    "sequentially append the specified Writables to the writer" in new SUT {
      __Exercise__
      sut << "4行目".getBytes(ISO2022) << sep.getBytes(ISO2022)
      closeIfCloseable(sut)
      __Verify__
      text(destPath, ISO2022) should equal(contentAsStringISO2022 + "4行目" + sep)
    }
  }

  def text(path: Path, charset: Charset): String = new String(Files.readAllBytes(path), charset)
}

trait CloseableOutputStreamWrapperLikeSpec[T <: OutputStreamWrapperLike[T]]
    extends OutputStreamWrapperLikeSpec[T]
    with CloseableWriterWrapperLikeSpec[T]
    with CloseableObjectOutputStreamWrapperLikeSpec
    with CloseableDataOutputStreamWrapperLikeSpec{

  private trait SUT{
    val destPath = GluinoPath.createTempFile()
    val sut = newOutputStreamWrapperLike(destPath)
  }

  "Methods of OutputStreamWrapperLike trait should not close reader after use" - {

    /** WriterWrapper#append() is not implicitly applied due to overloading */
    "OutputStreamWrapper#append() method" in new SUT{
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

class OutputStreamWrapperSpec
    extends CloseableOutputStreamWrapperLikeSpec[OutputStreamWrapper]
    with MockFactory{

  override protected def newOutputStreamWrapperLike(path: Path) =
    OutputStreamWrapper(path, true)

  private trait MockedOutputStreamWrapper{
    val os = mock[OutputStream]
    inSequence{
      (os.flush _).expects().anyNumberOfTimes()
      (os.close _).expects()
    }
    val sut = OutputStreamWrapper(os)
  }

  "withOutputStream() method should" - {

    "flush and close the stream after use" in new MockedOutputStreamWrapper {
      __Verify__
      sut.withOutputStream { _ => }
    }

    "close the stream when exception thrown" in new MockedOutputStreamWrapper {
      __Verify__
      try{
        sut.withOutputStream{ _ => throw new RuntimeException() }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }

  "withWriter() method should" - {

    "flush and close the stream after use" in new MockedOutputStreamWrapper {
      __Verify__
      sut.withWriter { _ => }
    }

    "close the stream when exception thrown" in new MockedOutputStreamWrapper {
      __Verify__
      try{
        sut.withWriter{ _ => throw new RuntimeException() }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }

  "withPrintWriter() method should" - {

    "flush and close the stream after use" in new MockedOutputStreamWrapper {
      __Verify__
      sut.withPrintWriter { _ => }
    }

    "close the stream when exception thrown" in new MockedOutputStreamWrapper {
      __Verify__
      try{
        sut.withPrintWriter{ _ => throw new RuntimeException() }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }

  "withDataOutputStream() method should" - {

    "flush and close the stream after use" in new MockedOutputStreamWrapper {
      __Verify__
      sut.withDataOutputStream { _ => }
    }

    "close the stream when exception thrown" in new MockedOutputStreamWrapper {
      __Verify__
      try{
        sut.withDataOutputStream{ _ => throw new RuntimeException() }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }

  "withObjectOutputStream() method should" - {

    "flush and close the stream after use" in {
      __SetUp__
      val os = mock[OutputStream]
      inSequence {
        inAnyOrder {
          (os.write(_: Array[Byte], _: Int, _: Int)).expects(*, *, *).anyNumberOfTimes()
          (os.flush _).expects().anyNumberOfTimes()
        }
        (os.close _).expects()
      }
      val sut = OutputStreamWrapper(os)
      __Verify__
      sut.withObjectOutputStream { _ => }
    }

    "close the stream when exception thrown" in {
      __SetUp__
      val os = mock[OutputStream]
      inSequence {
        inAnyOrder {
          (os.write(_: Array[Byte], _: Int, _: Int)).expects(*, *, *).anyNumberOfTimes()
          (os.flush _).expects().anyNumberOfTimes()
        }
        (os.close _).expects()
      }
      val sut = OutputStreamWrapper(os)
      __Verify__
      try{
        sut.withObjectOutputStream{ _ => throw new RuntimeException() }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }
}
