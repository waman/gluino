package org.waman.gluino.io.objectstream

import java.io.ObjectInputStream
import java.nio.file.Path

import org.waman.gluino.io.GluinoIOCustomSpec

import scala.collection.mutable

trait ObjectInputStreamWrapperLikeSpec extends GluinoIOCustomSpec{

  protected def newObjectInputStreamWrapperLike(path: Path): ObjectInputStreamWrapperLike

  private trait SUT{
     val sut = newObjectInputStreamWrapperLike(readOnlyPathObjects)
  }

  "withObjectInputStream() method should" - {

    "close the stream after use" in new SUT {
      __Exercise__
      val result = sut.withObjectInputStream{ ois => ois }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new SUT{
      __Exercise__
      var result: ObjectInputStream = null
      try{
        sut.withObjectInputStream{ ois =>
          result = ois
          throw new RuntimeException()
        }
      }catch{
        case ex: RuntimeException =>
      }
      __Verify__
      result should be (closed)
    }

    "be able to use with the loan pattern" in new SUT{
      __SetUp__
      var result = new mutable.MutableList[Any]
      __Exercise__
      sut.withObjectInputStream { ois =>
        result += ois.readObject()
        result += ois.readObject()
        result += ois.readObject()
      }
      __Verify__
      result should contain theSameElementsInOrderAs contentObjects
    }
  }

  "eachObject() method should iterate objects read from the stream" in new SUT{
    __SetUp__
    var result = new mutable.MutableList[Any]
    __Exercise__
    sut.eachAnyRef(result += _)
    __Verify__
    result should contain theSameElementsInOrderAs contentObjects
  }
}

trait CloseableObjectInputStreamWrapperLikeSpec
    extends ObjectInputStreamWrapperLikeSpec

class ObjectInputStreamWrapperSpec
    extends CloseableObjectInputStreamWrapperLikeSpec{

  protected def newObjectInputStreamWrapperLike(path: Path) =
    ObjectInputStreamWrapper(path)
}
