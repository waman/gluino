package org.waman.gluino.io.objectstream

import java.io.ObjectOutputStream
import java.nio.file.{Files, Path}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIOCustomSpec
import org.waman.gluino.nio.GluinoPath

import scala.collection.mutable

trait ObjectInputStreamWrapperLikeSpec extends GluinoIOCustomSpec{

  protected def newObjectInputStreamWrapperLike(path: Path): ObjectInputStreamWrapperLike

  private val contentObjects = List("1", new Integer(2), BigDecimal(3))

  private trait SUT{
    val path = GluinoPath.createTempFile()
    initFile(path)
    val sut = newObjectInputStreamWrapperLike(path)
  }

  private def initFile(path: Path): Unit = {
    val oos = new ObjectOutputStream(Files.newOutputStream(path))
    contentObjects.foreach(oos.writeObject(_))
    oos.flush()
    oos.close()
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

trait CloseableObjectInputStreamWrapperLikeSpec
    extends ObjectInputStreamWrapperLikeSpec

class ObjectInputStreamWrapperSpec
    extends CloseableObjectInputStreamWrapperLikeSpec with MockFactory{

  protected def newObjectInputStreamWrapperLike(path: Path) =
    ObjectInputStreamWrapper(path)

  private trait SUT{
    val path = GluinoPath.createTempFile()
    initFile(path)
  }

  private def initFile(path: Path): Unit = {
    val oos = new ObjectOutputStream(Files.newOutputStream(path))
    oos.writeObject("content")
    oos.flush()
    oos.close()
  }

  "withObjectInputStream() method should" - {

    "close the stream after use" in new SUT{
      __SetUp__
      val input = Files.newInputStream(path)
      val sut = ObjectInputStreamWrapper(input)
      __Exercise__
      sut.withObjectInputStream { _ => }
      __Verify__
      input should be (closed)
    }
  }
}
