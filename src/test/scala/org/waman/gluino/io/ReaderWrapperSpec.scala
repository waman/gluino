package org.waman.gluino.io

import java.io.{BufferedReader, Reader}

import org.waman.gluino.GluinoCustomSpec

import scala.collection.mutable

class ReaderWrapperSpec extends GluinoCustomSpec with GluinoIO{

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
  
  "withReader() method should" - {
    "close reader after use" in {
      __SetUp__
      val reader = mock[Reader]
      (reader.close _).expects()
      __Exercise__
      reader.withReader{ r => }
      __Verify__
    }
  }

//  "***** text *****" - {
//    "eachChar() method iterate each character of File content" in new ReaderFixture{
//      var sum = 0
//      reader.eachChar(c => sum += c.asInstanceOf[Int])
//      sum should equal (contentAsString.foldLeft(0)((s, c) => s + c.asInstanceOf[Int]))
//    }
//
//    "text method return file content as String" in new ReaderFixture{
//      reader.text should be (contentAsString)
//    }
//  }

    "***** lines *****" - {
      "eachLines() method iterate each character of File content" - {
        "read lines one by one"  in new ReaderFixture{
          __Exercise__
          val sut = new mutable.ListBuffer[String]
          reader.eachLine(sut += _)
          __Verify__
          sut should contain theSameElementsInOrderAs content
        }

        "close the reader after use" in new ReaderFixture{
          __Exercise__
          reader.eachLine{ line => }
          __Verify__
          reader should be (closed)
        }
      }

      "readLines() method should" - {
        "read all lines in File" in new ReaderFixture{
          __Exercise__
          val sut = reader.readLines()
          __Verify__
          sut should contain theSameElementsInOrderAs content
        }

        "close the reader after use" in new ReaderFixture{
          __Exercise__
          reader.readLines()
          __Verify__
          reader should be (closed)
        }
      }
    }
}
