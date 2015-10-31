package org.waman.gluino.io

import java.io._

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.GluinoCustomSpec

class GluinoIOSpec extends GluinoCustomSpec with GluinoIO with MockFactory{

  "implicit conversions" - {

    "InputStream is implicitly converted to InputStreamWrapper" in {
      __SetUp__
      val is = mock[InputStream]
      __Verify__
      noException should be thrownBy { val isw: InputStreamWrapper = is }
    }

    "OutputStream is implicitly converted to OutputStreamWrapper" in {
      __SetUp__
      val os = mock[OutputStream]
      __Verify__
      noException should be thrownBy { val osw: OutputStreamWrapper = os }
    }

    "Reader is implicitly converted to ReaderWrapper" in {
      __SetUp__
      val reader = mock[Reader]
      __Verify__
      noException should be thrownBy { val rw: ReaderWrapper = reader }
    }

    "Writer is implicitly converted to WriterWrapper" in {
      __SetUp__
      val writer = mock[Writer]
      __Verify__
      noException should be thrownBy { val ww: WriterWrapper = writer }
    }

    "PrintWriter is implicitly converted to PrintWriterWrapper" in {
      __SetUp__
      val pw = new PrintWriter(mock[Writer])
      __Verify__
      noException should be thrownBy { val pww: PrintWriterWrapper = pw }
    }
  }
}
