package org.waman.gluino.io

import java.io.OutputStream
import java.nio.file.Path

import org.waman.gluino.io.GluinoIO.{lineSeparator => sep}
import org.waman.gluino.io.datastream.{CloseableDataOutputStreamWrapperLikeSpec, DataOutputStreamWrapperLikeSpec}
import org.waman.gluino.io.objectstream.{CloseableObjectOutputStreamWrapperLikeSpec, ObjectOutputStreamWrapperLikeSpec}

trait OutputStreamWrapperLikeSpec[T <: OutputStreamWrapperLike[T]]
    extends WriterWrapperLikeSpec[T]
    with ObjectOutputStreamWrapperLikeSpec
    with DataOutputStreamWrapperLikeSpec{

  protected def newOutputStreamWrapperLike(path: Path): T

  override protected def newWriterWrapperLike(path: Path) = newOutputStreamWrapperLike(path)
  override protected def newObjectOutputStreamWrapperLike(path: Path) = newOutputStreamWrapperLike(path)
  override protected def newDataOutputStreamWrapperLike(path: Path) = newOutputStreamWrapperLike(path)

  trait OutputStreamWrapperLikeFixture extends FileFixture{
    val sut = newOutputStreamWrapperLike(path)
  }

  trait OutputStreamWrapperLikeISO2022Fixture extends FileWithContentISO2022Fixture{
    val sut = newOutputStreamWrapperLike(path)
  }

  "withOutputStream() method should" - {

    "close the stream after use" in new OutputStreamWrapperLikeFixture {
      __Exercise__
      val result = sut.withOutputStream{ os => os }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new OutputStreamWrapperLikeFixture {
      __Exercise__
      var result: OutputStream = null
      try{
        sut.withOutputStream{ os =>
          result = os
          throw new RuntimeException()
        }
      }catch{
        case ex: RuntimeException =>
      }
      __Verify__
      result should be (closed)
    }

    "be able to use with the loan pattern" in new OutputStreamWrapperLikeFixture{
      __Exercise__
      sut.withOutputStream { os =>
        os.write(contentAsStringISO2022.getBytes(ISO2022))
      }
      __Verify__
      text(path, ISO2022) should equal (contentAsStringISO2022)
    }
  }

  // OutputStreamWrapper#append() is not implicitly applied due to overloading
  "append(Outputtable) method should append the specified bytes to the stream" in
    new OutputStreamWrapperLikeISO2022Fixture{
      __Exercise__
      sut.append("4行目".getBytes(ISO2022))
      closeIfCloseable(sut)
      __Verify__
      text(path, ISO2022) should equal (contentAsStringISO2022 + "4行目")
    }

  "<< operator for Outputtable should" - {

    "append the specified Outputtable to the stream" in new OutputStreamWrapperLikeISO2022Fixture {
      __Exercise__
      sut << "4行目".getBytes(ISO2022)
      closeIfCloseable(sut)
      __Verify__
      text(path, ISO2022) should equal(contentAsStringISO2022 + "4行目")
    }

    "sequentially append the specified Writables to the writer" in
      new OutputStreamWrapperLikeISO2022Fixture {
        __Exercise__
        sut << "4行目".getBytes(ISO2022) << sep.getBytes(ISO2022)
        closeIfCloseable(sut)
        __Verify__
        text(path, ISO2022) should equal(contentAsStringISO2022 + "4行目" + sep)
      }
  }
}

trait CloseableOutputStreamWrapperLikeSpec[T <: OutputStreamWrapperLike[T]]
    extends OutputStreamWrapperLikeSpec[T]
    with CloseableWriterWrapperLikeSpec[T]
    with CloseableObjectOutputStreamWrapperLikeSpec
    with CloseableDataOutputStreamWrapperLikeSpec{

  "Methods of OutputStreamWrapperLike trait should NOT close reader after use" - {

    /** WriterWrapper#append() is not implicitly applied due to overloading */
    "OutputStreamWrapper#append() method" in new OutputStreamWrapperLikeFixture{
      __Exercise__
      sut.append("first line.")
      __Verify__
      sut should be (opened)
    }

    "<< operator" in new OutputStreamWrapperLikeFixture{
      __Exercise__
      sut << "first line."
      __Verify__
      sut should be (opened)
    }
  }
}

class OutputStreamWrapperSpec
    extends CloseableOutputStreamWrapperLikeSpec[OutputStreamWrapper]{

  override protected def newOutputStreamWrapperLike(path: Path) =
    OutputStreamWrapper(path, append = true)
}
