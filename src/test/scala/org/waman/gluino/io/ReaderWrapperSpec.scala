package org.waman.gluino.io

import java.io.{BufferedReader, Reader}
import java.nio.file.Files

class ReaderWrapperSpec extends CloseableReaderWrapperLikeSpec[ReaderWrapper] with GluinoIO{

  override def newReaderWrapperLike = ReaderWrapper(Files.newBufferedReader(readOnlyPath))

  "***** Factory method *****" - {

    "the Reader is retained directly by wrapper if an instance of BufferedReader" in {
      __SetUp__
      val reader = new BufferedReader(mock[Reader])
      __Exercise__
      val wrapper = ReaderWrapper(reader)
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
  
  "withReader() method should" - {
    "close reader after use" in {
      __SetUp__
      val reader = mock[Reader]
      (reader.close _).expects()
      __Exercise__
      reader.withReader{ _ => }
      __Verify__
    }
  }
}
