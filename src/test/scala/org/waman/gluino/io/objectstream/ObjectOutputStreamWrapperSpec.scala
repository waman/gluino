package org.waman.gluino.io.objectstream

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Files, Path}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIOCustomSpec

import scala.collection.mutable

trait ObjectOutputStreamWrapperLikeSpec extends GluinoIOCustomSpec{

  protected def newObjectOutputStreamWrapperLike(path: Path): ObjectOutputStreamWrapperLike

  private trait SUT extends DestFileFixture{
    val sut = newObjectOutputStreamWrapperLike(destPath)
  }

  "withObjectOutputStream() method should" - {

    "close the stream after use" in new SUT {
      __Exercise__
      val result = sut.withObjectOutputStream{ oos => oos }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new SUT {
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

    "be able to use with the loan pattern" in new SUT{
      __Exercise__
      sut.withObjectOutputStream { oos =>
        contentObjects.foreach(oos.writeObject(_))
      }
      __Verify__
      objects(destPath) should contain theSameElementsInOrderAs contentObjects
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
