package org.waman.gluino.io.datastream

import java.io.{DataOutputStream, InputStream}
import java.nio.file.{Files, Path}

import org.waman.gluino.io.GluinoIOCustomSpec

import scala.collection.mutable

trait DataInputStreamWrapperLikeSpec extends GluinoIOCustomSpec with DataStreamFixture{

  protected def newDataInputStreamWrapperLike(path: Path): DataInputStreamWrapperLike

  trait DataInputStreamWrapperLikeFixture extends DestFileFixture{
    val sut = newDataInputStreamWrapperLike(destPath)
  }

  trait DataInputStreamWrapperLikeWithContentFixture extends DataFileFixture{
    val sut = newDataInputStreamWrapperLike(dataPath)
  }

  private def initFile(path: Path): Unit = {
    val dos = new DataOutputStream(Files.newOutputStream(path))

    dos.writeInt(1)
    dos.writeLong(2L)
    dos.writeDouble(3.0d)
    dos.writeUTF("UTF")
    dos.writeBytes("string")

    dos.flush()
    dos.close()
  }

  "withDataInputStream() method should" - {

    "close the stream after use" in new DataInputStreamWrapperLikeFixture{
      __Exercise__
      val result = sut.withDataInputStream{ dis => dis }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new DataInputStreamWrapperLikeFixture{
      __Exercise__
      var result: InputStream = null
      try{
        sut.withDataInputStream{ dis =>
          result = dis
          throw new RuntimeException()
        }
      }catch{
        case ex: RuntimeException =>
      }
      __Verify__
      result should be (closed)
    }

    "be able to use with the loan pattern" in new DataInputStreamWrapperLikeWithContentFixture {
      __SetUp__
      val result = new mutable.MutableList[Any]()
      __Exercise__
      sut.withDataInputStream { dis =>
        result += dis.readInt()
        result += dis.readLong()
        result += dis.readDouble()
        result += new String(dis.readUTF())

        val bytes = new Array[Byte](6)
        dis.read(bytes)
        result += new String(bytes)
      }
      __Verify__
      result should contain theSameElementsInOrderAs
        List(1, 2L, 3.0d, "UTF", "string")
    }
  }
}

trait CloseableDataInputStreamWrapperLikeSpec
    extends DataInputStreamWrapperLikeSpec

class DataInputStreamWrapperSpec
    extends CloseableDataInputStreamWrapperLikeSpec{

  override protected def newDataInputStreamWrapperLike(path: Path) =
    DataInputStreamWrapper(path)
}
