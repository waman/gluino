package org.waman.gluino.io.objectstream

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.math.BigDecimal
import java.nio.file.{Files, Path}

import org.waman.gluino.io.GluinoIOCustomSpec
import org.waman.gluino.nio.GluinoPath

import scala.collection.mutable

trait ObjectInputStreamWrapperLikeSpec extends GluinoIOCustomSpec{

  protected def newObjectInputStreamWrapperLike(path: Path): ObjectInputStreamWrapperLike

  val contentObjects = List("first line.", new Integer(2), new BigDecimal("3"))

  trait SUT{
    val path = Files.createTempFile(null, null)
    val oos = new ObjectOutputStream(Files.newOutputStream(path))
    contentObjects.foreach(oos.writeObject(_))
    oos.flush()
    oos.close()

    val sut = newObjectInputStreamWrapperLike(path)
  }

  "withObjectInputStream() method should be able to use with the loan pattern" in new SUT{
    __SetUp__
    var result = new mutable.MutableList[Any]
    __Exercise__
    sut.withObjectInputStream{ ois =>
      result += ois.readObject()
      result += ois.readObject()
      result += ois.readObject()
    }
    __Verify__
    result should contain theSameElementsInOrderAs contentObjects
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

class ObjectInputStreamWrapperSpec extends ObjectInputStreamWrapperLikeSpec with GluinoPath{

  protected def newObjectInputStreamWrapperLike(path: Path): ObjectInputStreamWrapperLike =
    new ObjectInputStreamWrapper(new ObjectInputStream(Files.newInputStream(path)))

  "write many kinds of primitive data" in {
    val path = createTempFile()
    path.withObjectOutputStream{ oos =>
      oos << 1
      oos << 2L
      oos <++ 3
      oos <# 4
      oos <## 5
    }

    path.eachByte{ b =>
      print(b)
      print(" ")
    }
  }
}
