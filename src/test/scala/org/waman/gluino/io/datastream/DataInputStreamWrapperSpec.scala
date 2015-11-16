package org.waman.gluino.io.datastream

import java.io.{DataOutputStream, InputStream}
import java.nio.file.{Files, Path}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIOCustomSpec
import org.waman.gluino.nio.GluinoPath

import scala.collection.mutable

trait DataInputStreamWrapperLikeSpec extends GluinoIOCustomSpec{

  protected def newDataInputStreamWrapperLike(path: Path): DataInputStreamWrapperLike

  private trait SUT{
    val path = GluinoPath.createTempFile()
    initFile(path)

    val sut = newDataInputStreamWrapperLike(path)
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

  "withDataInputStream() method should be able to use with the loan pattern" in new SUT{
    __SetUp__
    val result = new mutable.MutableList[Any]()
    __Exercise__
    sut.withDataInputStream{ dis =>
      result += dis.readInt()
      result += dis.readLong()
      result += dis.readDouble()
      result += new String(dis.readUTF())

      val bytes = new Array[Byte](6)
      dis.read(bytes)
      result += new String(bytes)
    }
    __Verify__
    result should contain theSameElementsInOrderAs List(1, 2L, 3.0d, "UTF", "string")
  }
}

trait CloseableDataInputStreamWrapperLikeSpec
    extends DataInputStreamWrapperLikeSpec{

  "Methods of DataInputStreamWrapperLike trait should properly close stream after use" - {
  }
}

class DataInputStreamWrapperSpec extends CloseableDataInputStreamWrapperLikeSpec with MockFactory{

  override protected def newDataInputStreamWrapperLike(path: Path) = DataInputStreamWrapper(path)

  "withDataInputStream() method should" - {

    "close the stream after use" in {
      __SetUp__
      val is = mock[InputStream]
      (is.close _).expects()
      val sut = DataInputStreamWrapper(is)
      __Verify__
      sut.withDataInputStream { _ => }
    }

    "close the stream when exception thrown" in {
      __SetUp__
      val is = mock[InputStream]
      (is.close _).expects()
      val sut = DataInputStreamWrapper(is)
      __Verify__
      try {
        sut.withDataInputStream { _ => throw new RuntimeException() }
      }catch{
        case ex: RuntimeException =>
      }
    }
  }
}
