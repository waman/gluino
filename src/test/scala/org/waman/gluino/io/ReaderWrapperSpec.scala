package org.waman.gluino.io

import java.io.{BufferedReader, Reader}
import java.nio.file.Files

import org.waman.gluino.GluinoCustomSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import org.scalatest.LoneElement._

class ReaderWrapperSpec extends GluinoCustomSpec with GluinoIO{

  "***** Factory method *****" - {

    "the Reader is retained directly by wrapper if an instance of BufferedReader" in {
      __SetUp__
      val reader = new BufferedReader(mock[Reader])
      __Exercise__
      val wrapper: ReaderWrapper = ReaderWrapper(reader)
      __Verify__
      wrapper.getReader should be (a [BufferedReader])
      wrapper.getReader should be theSameInstanceAs reader
    }

    "the Reader is wrapped by BufferedReader and then retained if not an instance of BufferedReader" in {
      __SetUp__
      val reader = mock[Reader]
      __Exercise__
      val wrapper = ReaderWrapper(reader)
      __Verify__
      wrapper.getReader should be (a [BufferedReader])
      wrapper.getReader should not be theSameInstanceAs (reader)
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

  "***** text *****" - {
    "eachChar() method should" - {

      "close the reader after use" in new ReaderFixture {
        __Exercise__
        reader.eachChar{ _ => }
        __Verify__
        reader should be (closed)
      }

      "read characters one by one" in new ReaderFixture {
        __SetUp__
        var sum = 0
        __Exercise__
        reader.eachChar(c => sum += c.asInstanceOf[Int])
        __Verify__
        sum should equal(contentAsString.foldLeft(0)(_ + _.asInstanceOf[Int]))
      }
    }

    "transformChar() method should" - {

      "close the reader and writer after use" in new ReaderWriterFixture{
        __Exercise__
        reader.transformChar(writer)(c => c)
        __Verify__
        reader should be (closed)
        writer should be (closed)
      }

      "transform each char and write down to the specified writer" in new ReaderWriterFixture{
        __Exercise__
        reader.transformChar(writer){
          case v if "aeiou" contains v => Character.toUpperCase(v)
          case c => c
        }
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          List("fIrst lInE.", "sEcOnd lInE.", "thIrd lInE.")
      }
    }

    "text method should" - {
      "close the reader after use" in new ReaderFixture {
        __Exercise__
        reader.text
        __Verify__
        reader should be(closed)
      }

      "return file content as String" in new ReaderFixture{
        __Verify__
        reader.text should be (contentAsString)
      }
    }
  }

  "***** lines *****" - {
    "eachLines(String => Unit) method should" - {

      "close the reader after use" in new ReaderFixture{
        __Exercise__
        reader.eachLine{ _ => }
        __Verify__
        reader should be (closed)
      }

      "read lines one by one" in new ReaderFixture{
        __SetUp__
        val sut = new mutable.ListBuffer[String]
        __Exercise__
        reader.eachLine(sut += _)
        __Verify__
        sut should contain theSameElementsInOrderAs content
      }
    }

    "eachLine(Int)((String, Int) => Unit) method should" - {

      "close the reader after use" in new ReaderFixture{
        __Exercise__
        reader.eachLine(1){ (_, _) => }
        __Verify__
        reader should be (closed)
      }

      "read lines one by one with line number. The first arg specifies the starting number." in
          new ReaderFixture{
        __SetUp__
        val sut = new mutable.ListBuffer[String]
        __Exercise__
        reader.eachLine(1)((s, n) => sut += s"""$n. $s""")
        __Verify__
        sut should contain theSameElementsInOrderAs List(
          "1. first line.",
          "2. second line.",
          "3. third line."
        )
      }

      "read lines one by one with line number. if the first arg omitted, line number starts at 0." in
          new ReaderFixture {
        __SetUp__
        val sut = new mutable.ListBuffer[String]
        __Exercise__
        reader.eachLine()((s, n) => sut += s"""$n. $s""")
        __Verify__
        sut should contain theSameElementsInOrderAs List(
          "0. first line.",
          "1. second line.",
          "2. third line."
        )
      }
    }

    "splitEachLine() method should" - {

      "close the reader after use" in new ReaderFixture{
        __Exercise__
        reader.splitEachLine("\\s+".r){_ => }
        __Verify__
        reader should be (closed)
      }

      "split each line by the specified regex" in new ReaderFixture{
        __SetUp__
        val sut = new ListBuffer[String]()
        __Exercise__
        reader.splitEachLine("\\s+".r)(sut ++= _)
        __Verify__
        sut should contain theSameElementsInOrderAs
          List("first", "line.", "second", "line.", "third", "line.")
      }
    }

    "filterLine(String => Boolean) method should" - {

      "close the reader after use" in new ReaderWriterFixture{
        __Exercise__
        val writable = reader.filterLine{ _ => true }
        writable.writeTo(writer)
        __Verify__
        reader should be (closed)
      }

      "filter line and write down to the specified writer" in new ReaderWriterFixture{
        __Exercise__
        val writable = reader.filterLine(_ contains "second")
        writable.writeTo(writer)
        writer.close()
        __Verify__
        Files.readAllLines(destPath).loneElement should equal ("second line.")
      }
    }

    "filterLine(Writer)(String => Boolean) method should" - {

      "close the reader and writer after use" in new ReaderWriterFixture{
        __Exercise__
        reader.filterLine(writer){ _ => true }
        __Verify__
        reader should be (closed)
        writer should be (closed)
      }

      "filter line and write down to the specified writer" in new ReaderWriterFixture{
        __Exercise__
        reader.filterLine(writer)(_ contains "second")
        __Verify__
        Files.readAllLines(destPath).loneElement should equal ("second line.")
      }
    }

    "transformLine() method should" - {

      "close the reader and writer after use" in new ReaderWriterFixture{
        __Exercise__
        reader.transformLine(writer)(_.toUpperCase)
        __Verify__
        reader should be (closed)
        writer should be (closed)
      }

      "transform each line and write down to the specified writer" in new ReaderWriterFixture{
        __Exercise__
        reader.transformLine(writer)(_.toUpperCase)
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          List("FIRST LINE.", "SECOND LINE.", "THIRD LINE.")
      }
    }

    "readLines() method should" - {

      "close the reader after use" in new ReaderFixture{
        __Exercise__
        reader.readLines()
        __Verify__
        reader should be (closed)
      }

      "read all lines in File" in new ReaderFixture{
        __Exercise__
        val sut = reader.readLines()
        __Verify__
        sut should contain theSameElementsInOrderAs content
      }
    }
  }
}
