package org.waman.gluino.io.objectstream

import java.io.{EOFException, ObjectInputStream}
import java.nio.file.Path

import org.waman.gluino.io.GluinoIOCustomSpec
import org.waman.gluino.number.GluinoNumber

import scala.collection.mutable

trait ObjectInputStreamWrapperLikeSpec extends GluinoIOCustomSpec
    with ObjectStreamFixture with GluinoNumber{

  protected def newObjectInputStreamWrapperLike(path: Path): ObjectInputStreamWrapperLike

  trait ObjectInputStreamWrapperLikeFixture{
     val sut = newObjectInputStreamWrapperLike(readOnlyObjectPath)
  }

  "withObjectInputStream() method should" - {

    "close the stream after use" in new ObjectInputStreamWrapperLikeFixture {
      __Exercise__
      val result = sut.withObjectInputStream{ ois => ois }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new ObjectInputStreamWrapperLikeFixture{
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

    "be able to use with the loan pattern" in new ObjectInputStreamWrapperLikeFixture{
      __SetUp__
      var result = new mutable.MutableList[Any]
      __Exercise__
      sut.withObjectInputStream { ois =>
        3 times {
          result += ois.readObject()
        }
      }
      __Verify__
      result should contain theSameElementsInOrderAs objectContent
    }
  }

  "eachAnyRef() method should iterate objects read from the stream" in
    new ObjectInputStreamWrapperLikeFixture{
      __SetUp__
      var result = new mutable.MutableList[Any]
      __Exercise__
      sut.eachAnyRef(result += _)
      __Verify__
      result should contain theSameElementsInOrderAs objectContent
    }

  "readAnyRefs(Int) should be" - {

    "read the specified number of AnyRefs (objects)" in new ObjectInputStreamWrapperLikeFixture{
      __Exercise__
      val result = sut.readAnyRefs(3)
      __Verify__
      result.size should equal (3)
      result should contain theSameElementsInOrderAs objectContent
    }

    """throw an EOFException if the specified integer is bigger than
      | the number of objects retained in the file""".stripMargin in
      new ObjectInputStreamWrapperLikeFixture{
        __Verify__
        an [EOFException] should be thrownBy {
          sut.readAnyRefs(10)
        }
      }

    "throw an IllegalArgumentException if the specified integer is negative" in
      new ObjectInputStreamWrapperLikeFixture{
        __Verify__
        an [IllegalArgumentException] should be thrownBy {
          sut.readAnyRefs(-1)
        }
    }
  }
}

trait CloseableObjectInputStreamWrapperLikeSpec
    extends ObjectInputStreamWrapperLikeSpec{

  "Methods of ObjectInputStreamWrapperLike trait should properly close the stream after use" - {

    "eachAnyRef() method" in new ObjectInputStreamWrapperLikeFixture{
      __Exercise__
      sut.eachAnyRef{ _ => }
      __Verify__
      sut should be (closed)
    }

    "readAnyRefs() method" in new ObjectInputStreamWrapperLikeFixture{
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
