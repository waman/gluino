package org.waman.gluino.io.objectstream

import java.io.OutputStream
import java.nio.file.Path

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.io.GluinoIOCustomSpec

trait ObjectOutputStreamWrapperLikeSpec extends GluinoIOCustomSpec{

  protected def newObjectOutputStreamWrapperLike(path: Path): ObjectOutputStreamWrapperLike
}

trait CloseableObjectOutputStreamWrapperLikeSpec
    extends ObjectOutputStreamWrapperLikeSpec with MockFactory{

  "Methods of ObjectOutputStreamWrapperLike trait should properly flush and close the stream after use" - {}
}

class ObjectOutputStreamWrapperSpec extends CloseableObjectOutputStreamWrapperLikeSpec{

  override protected def newObjectOutputStreamWrapperLike(path: Path) =
    ObjectOutputStreamWrapper(path)

  "withObjectOutputStream() method should flush and close the stream after use" in {
    __SetUp__
    val os = mock[OutputStream]
    inSequence{
      inAnyOrder{
        (os.write (_: Array[Byte], _: Int, _: Int)).expects(*, *, *).anyNumberOfTimes()
        (os.flush _).expects().anyNumberOfTimes()
      }
      (os.close _).expects()
    }
    val sut = ObjectOutputStreamWrapper(os)
    __Verify__
    sut.withObjectOutputStream{ _ => }
  }
}
