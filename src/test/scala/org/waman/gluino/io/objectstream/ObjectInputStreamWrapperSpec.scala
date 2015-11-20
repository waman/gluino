package org.waman.gluino.io.objectstream

import java.io.{EOFException, ObjectInputStream}
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

  "eachAnyRef() method should iterate objects read from the stream" in new SUT{
    __SetUp__
    var result = new mutable.MutableList[Any]
    __Exercise__
    sut.eachAnyRef(result += _)
    __Verify__
    result should contain theSameElementsInOrderAs contentObjects
  }

  "readAnyRefs(Int) should be" - {

    "read the specified number of AnyRefs (objects)" in new SUT{
      __Exercise__
      val result = sut.readAnyRefs(3)
      __Verify__
      result.size should equal (3)
      result should contain theSameElementsInOrderAs contentObjects
    }

    "throw an EOFException if the specified integer is bigger than the number of objects retained in the file" in new SUT{
      __Verify__
      an [EOFException] should be thrownBy {
        sut.readAnyRefs(10)
      }
    }

    "throw an IllegalArgumentException if the specified integer is negative" in new SUT{
      __Verify__
      an [IllegalArgumentException] should be thrownBy {
        sut.readAnyRefs(-1)
      }
    }
  }
}

trait CloseableObjectInputStreamWrapperLikeSpec
    extends ObjectInputStreamWrapperLikeSpec{

  private trait SUT{
    val sut = newObjectInputStreamWrapperLike(readOnlyPathObjects)
  }

  "Methods of ObjectInputStreamWrapperLike trait should properly close the stream after use" - {

    "eachAnyRef() method" in new SUT{
      __Exercise__
      sut.eachAnyRef{ _ => }
      __Verify__
      sut should be (closed)
    }

    "readAnyRefs() method" in new SUT{
      __Exercise__
      sut.readAnyRefs(1)
      __Verify__
      sut should be (closed)
    }
  }
}

class ObjectInputStreamWrapperSpec
    extends CloseableObjectInputStreamWrapperLikeSpec{

  protected def newObjectInputStreamWrapperLike(path: Path) =
    ObjectInputStreamWrapper(path)
}
