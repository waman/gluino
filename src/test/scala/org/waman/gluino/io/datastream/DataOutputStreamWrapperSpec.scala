package org.waman.gluino.io.datastream

import java.io.DataOutputStream
import java.nio.file.{Files, Path}

import org.waman.gluino.io.GluinoIOCustomSpec

trait DataOutputStreamWrapperLikeSpec extends GluinoIOCustomSpec with DataStreamFixture{

  protected def newDataOutputStreamWrapperLike(path: Path): DataOutputStreamWrapperLike

  trait DataOutputStreamWrapperLikeFixture extends DestFileFixture{
    val sut = newDataOutputStreamWrapperLike(destPath)
  }

  "withDataOutputStream() method should" - {

    "close the stream after use" in new DataOutputStreamWrapperLikeFixture {
      __Exercise__
      val result = sut.withDataOutputStream{ dos => dos }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new DataOutputStreamWrapperLikeFixture {
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

    "be able to use with the loan pattern" in new DataOutputStreamWrapperLikeFixture{
      __Exercise__
      sut.withDataOutputStream { dos =>
        dos.writeInt(1)
        dos.writeLong(2L)
        dos.writeDouble(3.0d)
        dos.writeUTF("UTF")
        dos.writeBytes("string")
      }
      __Verify__
      Files.readAllBytes(destPath) should equal (Files.readAllBytes(readOnlyDataPath))
    }
  }
}

trait CloseableDataOutputStreamWrapperLikeSpec
    extends DataOutputStreamWrapperLikeSpec

class DataOutputStreamWrapperSpec extends CloseableDataOutputStreamWrapperLikeSpec{

  override protected def newDataOutputStreamWrapperLike(path: Path) =
    DataOutputStreamWrapper(path)
}
