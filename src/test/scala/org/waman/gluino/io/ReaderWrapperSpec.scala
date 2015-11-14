package org.waman.gluino.io

import java.io.{BufferedReader, Reader}
import java.nio.file.Files

import org.scalatest.LoneElement._

import scala.collection.mutable

trait ReaderWrapperLikeSpec[T <: ReaderWrapperLike] extends GluinoIOCustomSpec{

  def newReaderWrapperLike: T

  private trait SUT {
    val sut = newReaderWrapperLike
  }

  "withReader() method should be able to use with the loan pattern" in new SUT{
    __SetUp__
    val result = new mutable.StringBuilder
    __Exercise__
    sut.withReader{ r =>
      var i = r.read()
      while(i != -1){
        result += i.asInstanceOf[Char]
        i = r.read()
      }
    }
    __Verify__
    result.toString should equal (contentAsString)
  }

  "***** text *****" - {
    "eachChar() method should read characters one by one" in new SUT{
      __SetUp__
      var sum = 0
      __Exercise__
      sut.eachChar(c => sum += c.asInstanceOf[Int])
      __Verify__
      sum should equal(contentAsString.foldLeft(0)(_ + _.asInstanceOf[Int]))
    }

    "transformChar() method should transform each char and write down to the specified writer" in new SUT{
      new WriterFixture {
        __Exercise__
        sut.transformChar(writer){
          case v if "aeiou" contains v => Character.toUpperCase(v)
          case c => c
        }
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          List("fIrst lInE.", "sEcOnd lInE.", "thIrd lInE.")
      }
    }

    "text method should return file content as String" in new SUT{
      __Verify__
      sut.text should be (contentAsString)
    }
  }

  "***** lines *****" - {

    "eachLines(String => Unit) method should read lines one by one" in new SUT{
      __SetUp__
      val result = new mutable.ListBuffer[String]
      __Exercise__
      sut.eachLine(result += _)
      __Verify__
      result should contain theSameElementsInOrderAs content
    }

    "eachLine(Int)((String, Int) => Unit) method should" - {

      "read lines one by one with line number. The first arg specifies the starting number." in new SUT{
        __SetUp__
        val result = new mutable.ListBuffer[String]
        __Exercise__
        sut.eachLine(1)((s, n) => result += s"""$n. $s""")
        __Verify__
        result should contain theSameElementsInOrderAs List(
          "1. first line.",
          "2. second line.",
          "3. third line."
        )
      }

      "read lines one by one with line number. if the first arg omitted, line number starts at 0." in new SUT{
        __SetUp__
        val result = new mutable.ListBuffer[String]
        __Exercise__
        sut.eachLine()((s, n) => result += s"""$n. $s""")
        __Verify__
        result should contain theSameElementsInOrderAs List(
          "0. first line.",
          "1. second line.",
          "2. third line."
        )
      }
    }

    "splitEachLine() method should split each line by the specified regex" in new SUT{
      __SetUp__
      val result = new mutable.ListBuffer[String]()
      __Exercise__
      sut.splitEachLine("\\s+".r)(result ++= _)
      __Verify__
      result should contain theSameElementsInOrderAs
        List("first", "line.", "second", "line.", "third", "line.")
    }

    "filterLine(String => Boolean) method should filter line and write down to the specified writer" in new SUT {
      new WriterFixture {
        __Exercise__
        val writable = sut.filterLine(_ contains "second")
        writable.writeTo(writer)
        writer.close()
        __Verify__
        Files.readAllLines(destPath).loneElement should equal("second line.")
      }
    }

    "filterLine(Writer)(String => Boolean) method should filter line and write down to the specified writer" in
      new SUT{
        new WriterFixture {
          __Exercise__
          sut.filterLine(writer)(_ contains "second")
          __Verify__
          Files.readAllLines(destPath).loneElement should equal ("second line.")
        }
      }

    "transformLine() method should transform each line and write down to the specified writer" in new SUT{
      new WriterFixture {
        __Exercise__
        sut.transformLine(writer)(_.toUpperCase)
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          List("FIRST LINE.", "SECOND LINE.", "THIRD LINE.")
      }
    }

    "readLines method should read all lines in File" in new SUT{
      __Exercise__
      val lines = sut.readLines
      __Verify__
      lines should contain theSameElementsInOrderAs content
    }
  }
}

trait CloseableReaderWrapperLikeSpec[T <: ReaderWrapperLike]
  extends ReaderWrapperLikeSpec[T]{

  private trait SUT{
    val sut = newReaderWrapperLike
  }

  "Methods of ReaderWrapperLike trait should properly close reader after use" - {

    "***** text *****" - {
      "eachChar() method" in new SUT{
        __Exercise__
        sut.eachChar { _ => }
        __Verify__
        sut should be (closed)
      }

      "transformChar() method" in new SUT{
        new WriterFixture {
          __Exercise__
          sut.transformChar(writer)(c => c)
          __Verify__
          sut should be (closed)
          writer should be (closed)
        }
      }

      "text method" in new SUT{
        __Exercise__
        sut.text
        __Verify__
        sut should be(closed)
      }
    }

    "***** lines *****" - {

      "eachLine() method" in new SUT{
        __Exercise__
        sut.eachLine{ _ => }
        __Verify__
        sut should be (closed)
      }

      "eachLine(Int)((String, Int) => Unit) method" in new SUT{
        __Exercise__
        sut.eachLine(1){ (_, _) => }
        __Verify__
        sut should be (closed)
      }

      "splitEachLine()" in new SUT{
        __Exercise__
        sut.splitEachLine("\\s+".r){_ => }
        __Verify__
        sut should be (closed)
      }

      "filterLine(String => Boolean) method " in new SUT{
        new WriterFixture {
          __Exercise__
          val writable = sut.filterLine{ _ => true }
          writable.writeTo(writer)
          __Verify__
          sut should be (closed)
        }
      }

      "filterLine(Writer)(String => Boolean) method" in new SUT{
        new WriterFixture {
          __Exercise__
          sut.filterLine(writer){ _ => true }
          __Verify__
          sut should be (closed)
          writer should be (closed)
        }
      }

      "transformLine() method" in new SUT{
        new WriterFixture {
          __Exercise__
          sut.transformLine(writer)(_.toUpperCase)
          __Verify__
          sut should be (closed)
          writer should be (closed)
        }
      }

      "readLines" in new SUT{
        new ReaderFixture{
          __Exercise__
          sut.readLines
          __Verify__
          sut should be (closed)
        }
      }
    }
  }
}

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
