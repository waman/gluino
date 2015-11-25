package org.waman.gluino.io.objectstream

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Files, Path}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIOCustomSpec

import scala.collection.mutable

trait ObjectOutputStreamWrapperLikeSpec extends GluinoIOCustomSpec with ObjectStreamFixture{

  protected def newObjectOutputStreamWrapperLike(path: Path): ObjectOutputStreamWrapperLike

  trait ObjectOutputStreamWrapperLikeFixture extends FileFixture{
    val sut = newObjectOutputStreamWrapperLike(path)
  }

  "withObjectOutputStream() method should" - {

    "close the stream after use" in new ObjectOutputStreamWrapperLikeFixture {
      __Exercise__
      val result = sut.withObjectOutputStream{ oos => oos }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new ObjectOutputStreamWrapperLikeFixture {
      __Exercise__
      var result: ObjectOutputStream = null
      try{
        sut.withObjectOutputStream{ oos =>
          result = oos
          throw new RuntimeException()
        }
      }catch{
        case ex: RuntimeException =>
      }
      __Verify__
      result should be (closed)
    }

    "be able to use with the loan pattern" in new ObjectOutputStreamWrapperLikeFixture{
      __Exercise__
      sut.withObjectOutputStream { oos =>
        objectContent.foreach(oos.writeObject(_))
      }
      __Verify__
      objects(path) should contain theSameElementsInOrderAs objectContent
    }
  }

  def objects(path: Path): Seq[AnyRef] = {
    val result = new mutable.MutableList[AnyRef]
    val ois = new ObjectInputStream(Files.newInputStream(path))
    result += ois.readObject()
    result += ois.readObject()
    result += ois.readObject()
    ois.close()
    result
  }
}

trait CloseableObjectOutputStreamWrapperLikeSpec
    extends ObjectOutputStreamWrapperLikeSpec

class ObjectOutputStreamWrapperSpec
    extends CloseableObjectOutputStreamWrapperLikeSpec with MockFactory{

  override protected def newObjectOutputStreamWrapperLike(path: Path) =
    ObjectOutputStreamWrapper(path)
}
