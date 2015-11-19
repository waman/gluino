package org.waman.gluino.io.datastream

import java.io.DataOutputStream
import java.nio.file.Path

import org.waman.gluino.io.GluinoIOCustomSpec

trait DataOutputStreamWrapperLikeSpec extends GluinoIOCustomSpec{

  protected def newDataOutputStreamWrapperLike(path: Path): DataOutputStreamWrapperLike

  private trait SUT extends DestFileFixture{
    val sut = newDataOutputStreamWrapperLike(destPath)
  }

  "withDataOutputStream() method should" - {

    "close the stream after use" in new SUT {
      __Exercise__
      val result = sut.withDataOutputStream{ dos => dos }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new SUT {
      __Exercise__
      var result: DataOutputStream = null
      try{
        sut.withDataOutputStream{ dos =>
          result = dos
          throw new RuntimeException()
        }
      }catch{
        case ex: RuntimeException =>
      }
      __Verify__
      result should be (closed)
    }
  }
}

trait CloseableDataOutputStreamWrapperLikeSpec
    extends DataOutputStreamWrapperLikeSpec

class DataOutputStreamWrapperSpec extends CloseableDataOutputStreamWrapperLikeSpec{

  override protected def newDataOutputStreamWrapperLike(path: Path) =
    DataOutputStreamWrapper(path)
}
