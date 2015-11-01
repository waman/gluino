package org.waman.gluino.io

import java.io.{BufferedReader, Reader}
import java.nio.file.Files

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.GluinoCustomSpec

class ReaderWrapperSpec extends GluinoCustomSpec with MockFactory with GluinoIO{

  "***** Factory method *****" - {

    "the Reader is retained directly by wrapper if an instance of BufferedReader" in {
      __SetUp__
      val reader = new BufferedReader(mock[Reader])
      __Exercise__
      val wrapper: ReaderWrapper = ReaderWrapper(reader)
      __Verify__
      wrapper.reader should be (a [BufferedReader])
      wrapper.reader should be theSameInstanceAs reader
    }

    "the Reader is wrapped by BufferedReader and then retained if not an instance of BufferedReader" in {
      __SetUp__
      val reader = mock[Reader]
      __Exercise__
      val wrapper = ReaderWrapper(reader)
      __Verify__
      wrapper.reader should be (a [BufferedReader])
      wrapper.reader should not be theSameInstanceAs (reader)
    }
  }
  
  trait ReaderFixture extends TempFileFixture{
    val reader = Files.newBufferedReader(path)
  }
  
  "withReader() method should" - {
    "close reader after use" in {
      __SetUp__
      val reader = mock[Reader]
      (reader.close _).expects()
      __Exercise__
      reader.withReader{ reader => }
      __Verify__
    }
  }
}
