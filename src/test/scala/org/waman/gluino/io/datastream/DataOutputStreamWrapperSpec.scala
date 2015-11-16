package org.waman.gluino.io.datastream

import java.io.OutputStream
import java.nio.file.Path

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIOCustomSpec

trait DataOutputStreamWrapperLikeSpec extends GluinoIOCustomSpec{

  protected def newDataOutputStreamWrapperLike(path: Path): DataOutputStreamWrapperLike
}

trait CloseableDataOutputStreamWrapperLikeSpec
    extends DataOutputStreamWrapperLikeSpec with MockFactory{

  "Methods of DataOutputStreamWrapperLike trait should properly flush and close stream after use" - {

    "withDataOutputStream() method" in {
      __SetUp__
      val os = mock[OutputStream]
      inSequence{
        (os.flush _).expects().anyNumberOfTimes()
        (os.close _).expects()
      }
      val sut = DataOutputStreamWrapper(os)
      __Exercise__
      sut.withDataOutputStream { _ => }
      __Verify__
    }
  }
}

class DataOutputStreamWrapperSpec extends CloseableDataOutputStreamWrapperLikeSpec{

  override protected def newDataOutputStreamWrapperLike(path: Path) =
    DataOutputStreamWrapper(path)
}
