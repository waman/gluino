package org.waman.gluino.io

import java.io.{BufferedReader, Reader}

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.GluinoCustomSpec

class ReaderWrapperSpec extends GluinoCustomSpec with MockFactory{

  "factory method" - {

    "Reader of method arg is wrapped directly" in {
      __SetUp__
      val reader = new BufferedReader(mock[Reader])
      __Exercise__
      val rw: ReaderWrapper = ReaderWrapper(reader)
      __Verify__
      rw.reader should be (a [BufferedReader])
      rw.reader should be theSameInstanceAs reader
    }

    "Reader of method arg is wrapped by BufferedReader if not instance of BufferedReader" in {
      __SetUp__
      val reader = mock[Reader]
      __Exercise__
      val rw = ReaderWrapper(reader)
      __Verify__
      rw.reader should be (a [BufferedReader])
      rw.reader should not be theSameInstanceAs (reader)
    }
  }
}
