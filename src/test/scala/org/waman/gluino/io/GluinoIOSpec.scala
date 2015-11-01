package org.waman.gluino.io

import java.io._
import java.nio.file.Files

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.GluinoCustomSpec

class GluinoIOSpec extends GluinoCustomSpec with GluinoIO with MockFactory{

  "implicit conversions" - {

    "InputStream is implicitly converted to InputStreamWrapper" in {
      __SetUp__
      val is = mock[InputStream]
      __Verify__
      "val wrapped: InputStreamWrapper = is" should compile
    }

    "OutputStream is implicitly converted to OutputStreamWrapper" in {
      __SetUp__
      val os = mock[OutputStream]
      __Verify__
      "val wrapped: OutputStreamWrapper = os" should compile
    }

    "Reader is implicitly converted to ReaderWrapper" in {
      __SetUp__
      val reader = mock[Reader]
      __Verify__
      "val wrapped: ReaderWrapper = reader" should compile
    }

    "Writer is implicitly converted to WriterWrapper" in {
      __SetUp__
      val writer = mock[Writer]
      __Verify__
      "val wrapped: WriterWrapper = writer" should compile
    }

    "PrintWriter is implicitly converted to PrintWriterWrapper" in {
      __SetUp__
      val pw = new PrintWriter(mock[Writer])
      __Verify__
      "val wrapped: PrintWriterWrapper = pw" should compile
    }
  }
}
