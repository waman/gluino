package org.waman.gluino.io

import java.io.{Writer, BufferedWriter}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.GluinoCustomSpec

class WriterWrapperSpec extends GluinoCustomSpec with MockFactory{

  "factory method" - {

    "Writer of method arg is wrapped directly" in {
      __SetUp__
      val writer = new BufferedWriter(mock[Writer])
      __Exercise__
      val ww: WriterWrapper = WriterWrapper(writer)
      __Verify__
      ww.writer should be (a [BufferedWriter])
      ww.writer should be theSameInstanceAs writer
    }

    "Writer of method arg is wrapped by BufferedWriter if not instance of BufferedWriter" in {
      __SetUp__
      val writer = mock[Writer]
      __Exercise__
      val ww = WriterWrapper(writer)
      __Verify__
      ww.writer should be (a [BufferedWriter])
      ww.writer should not be theSameInstanceAs (writer)
    }
  }

}
