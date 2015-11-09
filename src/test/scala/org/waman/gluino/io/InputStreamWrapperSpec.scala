package org.waman.gluino.io

import java.io.InputStream
import java.nio.file.Files

import org.scalatest.LoneElement._
import org.waman.gluino.GluinoIOCustomSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class InputStreamWrapperSpec extends GluinoIOCustomSpec with GluinoIO {

  "withInputStream() method should" - {
    "close stream after use" in {
      __SetUp__
      val is = mock[InputStream]
      (is.close _).expects()
      __Exercise__
      is.withInputStream { is => }
      __Verify__
    }

    "create InputStream and close after use" in new InputStreamFixture{
      __SetUp__
      var sum = 0
      __Exercise__
      input.withInputStream{ is =>
        var i = is.read()
        while(i != -1){
          sum += i
          i = is.read()
        }
      }
      __Verify__
      sum should equal (contentAsString.sum)
    }
  }

  "***** Bytes *****" - {
    "eachByte() method should" - {

      "close the input stream after use" in new InputStreamFixture {
        __Exercise__
        input.eachByte{ _ => }
        __Verify__
        input should be (closed)
      }

      "read bytes one by one" in new InputStreamFixture {
        __SetUp__
        var sum = 0
        __Exercise__
        input.eachChar(b => sum += b)
        __Verify__
        sum should equal(contentAsString.sum)
      }
    }

    "bytes method should" - {
      "close the input stream after use" in new InputStreamFixture {
        __Exercise__
        input.bytes
        __Verify__
        input should be(closed)
      }

      "return file content as Array[byte]" in new InputStreamFixture{
        __Verify__
        input.bytes should be (contentAsString.getBytes())
      }
    }
  }

  "****** Tests for methods from ReaderWrapperLike trait with encoding *****" - {

    "***** text *****" - {
      "eachChar() method should" - {

        "close the input stream after use" in new InputStreamISO2022Fixture {
          __Exercise__
          input.eachChar(ISO2022){ _ => }
          __Verify__
          input should be(closed)
        }

        "read characters one by one" in new InputStreamISO2022Fixture {
          __SetUp__
          var sum = 0
          __Exercise__
          input.eachChar(ISO2022)(c => sum += c.asInstanceOf[Int])
          __Verify__
          sum should equal(contentAsStringISO2022.foldLeft(0)(_ + _.asInstanceOf[Int]))
        }
      }

      "transformChar() method should" - {

        "close the input stream and writer after use" in new InputStreamISO2022Fixture {
          new WriterFixture {
            __Exercise__
            input.transformChar(writer, ISO2022)(c => c)
            __Verify__
            input should be(closed)
            writer should be(closed)
          }
        }

        "transform each char and write down to the specified writer" in new InputStreamISO2022Fixture {
          new WriterFixture {
            __Exercise__
            input.transformChar(writer, ISO2022) {
              case c if c == '行' => '番'
              case c => c
            }
            __Verify__
            Files.readAllLines(destPath) should contain theSameElementsInOrderAs
              List("1番目", "2番目", "3番目")
          }
        }
      }

      "text method should" - {
        "close the input stream after use" in new InputStreamISO2022Fixture {
          __Exercise__
          input.text(ISO2022)
          __Verify__
          input should be(closed)
        }

        "return file content as String" in new InputStreamISO2022Fixture {
          __Verify__
          input.text(ISO2022) should be(contentAsStringISO2022)
        }
      }
    }

    "***** lines *****" - {
      "eachLines(String => Unit) method should" - {

        "close the input stream after use" in new InputStreamISO2022Fixture {
          __Exercise__
          input.eachLine(ISO2022) { _ => }
          __Verify__
          input should be(closed)
        }

        "read lines one by one" in new InputStreamISO2022Fixture {
          __SetUp__
          val sut = new mutable.ListBuffer[String]
          __Exercise__
          input.eachLine(ISO2022)(sut += _)
          __Verify__
          sut should contain theSameElementsInOrderAs contentISO2022
        }
      }

      "eachLine(Int)((String, Int) => Unit) method should" - {

        "close the input stream after use" in new InputStreamISO2022Fixture {
          __Exercise__
          input.eachLine(1, ISO2022) { (_, _) => }
          __Verify__
          input should be(closed)
        }

        "read lines one by one with line number. The first arg specifies the starting number." in
          new InputStreamISO2022Fixture {
            __SetUp__
            val sut = new mutable.ListBuffer[String]
            __Exercise__
            input.eachLine(1, ISO2022)((s, n) => sut += s"""$n. $s""")
            __Verify__
            sut should contain theSameElementsInOrderAs List(
              "1. 1行目",
              "2. 2行目",
              "3. 3行目"
            )
          }
      }

      "splitEachLine() method should" - {

        "close the input stream after use" in new InputStreamISO2022Fixture {
          __Exercise__
          input.splitEachLine("\\s+".r, ISO2022) { _ => }
          __Verify__
          input should be(closed)
        }

        "split each line by the specified regex" in new InputStreamISO2022Fixture {
          __SetUp__
          val sut = new ListBuffer[String]()
          __Exercise__
          input.splitEachLine("行".r, ISO2022)(sut ++= _)
          __Verify__
          sut should contain theSameElementsInOrderAs
            List("1", "目", "2", "目", "3", "目")
        }
      }

      "filterLine(String => Boolean) method should" - {

        "close the input stream after use" in new InputStreamISO2022Fixture {
          new WriterFixture {
            __Exercise__
            val writable = input.filterLine(ISO2022) { _ => true }
            writable.writeTo(writer)
            __Verify__
            input should be(closed)
          }
        }

        "filter line and write down to the specified writer" in new InputStreamISO2022Fixture {
          new WriterFixture {
            __Exercise__
            val writable = input.filterLine(ISO2022)(_ contains "2")
            writable.writeTo(writer)
            writer.close()
            __Verify__
            Files.readAllLines(destPath).loneElement should equal("2行目")
          }
        }
      }

      "filterLine(Writer)(String => Boolean) method should" - {

        "close the input stream and writer after use" in new InputStreamISO2022Fixture {
          new WriterFixture {
            __Exercise__
            input.filterLine(writer, ISO2022) { _ => true }
            __Verify__
            input should be(closed)
            writer should be(closed)
          }
        }

        "filter line and write down to the specified writer" in new InputStreamISO2022Fixture {
          new WriterFixture {
            __Exercise__
            input.filterLine(writer, ISO2022)(_ contains "2")
            __Verify__
            Files.readAllLines(destPath).loneElement should equal("2行目")
          }
        }
      }

      "transformLine() method should" - {

        "close the input stream and writer after use" in new InputStreamISO2022Fixture {
          new WriterFixture {
            __Exercise__
            input.transformLine(writer, ISO2022)(_.toUpperCase)
            __Verify__
            input should be(closed)
            writer should be(closed)
          }
        }

        "transform each line and write down to the specified writer" in new InputStreamISO2022Fixture {
          new WriterFixture {
            __Exercise__
            input.transformLine(writer, ISO2022)(line => s"""[$line]""")
            __Verify__
            Files.readAllLines(destPath) should contain theSameElementsInOrderAs
              List("[1行目]", "[2行目]", "[3行目]")
          }
        }
      }

      "readLines method should" - {

        "close the input stream after use" in new InputStreamISO2022Fixture {
          __Exercise__
          input.readLines(ISO2022)
          __Verify__
          input should be(closed)
        }

        "read all lines in File" in new InputStreamISO2022Fixture {
          __Exercise__
          val sut = input.readLines(ISO2022)
          __Verify__
          sut should contain theSameElementsInOrderAs contentISO2022
        }
      }
    }
  }

  "********** Tests for methods from ReaderWrapperLike trait **********" - {

    "***** text *****" - {
      "eachChar() method should" - {

        "close the input stream after use" in new InputStreamFixture {
          __Exercise__
          input.eachChar{ _ => }
          __Verify__
          input should be (closed)
        }

        "read characters one by one" in new InputStreamFixture {
          __SetUp__
          var sum = 0
          __Exercise__
          input.eachChar(c => sum += c.asInstanceOf[Int])
          __Verify__
          sum should equal(contentAsString.foldLeft(0)(_ + _.asInstanceOf[Int]))
        }
      }

      "transformChar() method should" - {

        "close the input stream and writer after use" in new InputStreamFixture{
          new WriterFixture{
            __Exercise__
            input.transformChar(writer)(c => c)
            __Verify__
            input should be (closed)
            writer should be (closed)
          }

        }

        "transform each char and write down to the specified writer" in new InputStreamFixture{
          new WriterFixture{
            __Exercise__
            input.transformChar(writer){
              case v if "aeiou" contains v => Character.toUpperCase(v)
              case c => c
            }
            __Verify__
            Files.readAllLines(destPath) should contain theSameElementsInOrderAs
              List("fIrst lInE.", "sEcOnd lInE.", "thIrd lInE.")
          }
        }
      }

      "text method should" - {
        "close the input stream after use" in new InputStreamFixture {
          __Exercise__
          input.text
          __Verify__
          input should be(closed)
        }

        "return file content as String" in new InputStreamFixture{
          __Verify__
          input.text should be (contentAsString)
        }
      }
    }

    "***** lines *****" - {
      "eachLines(String => Unit) method should" - {

        "close the input stream after use" in new InputStreamFixture{
          __Exercise__
          input.eachLine{ _ => }
          __Verify__
          input should be (closed)
        }

        "read lines one by one" in new InputStreamFixture{
          __SetUp__
          val sut = new mutable.ListBuffer[String]
          __Exercise__
          input.eachLine(sut += _)
          __Verify__
          sut should contain theSameElementsInOrderAs content
        }
      }

      "eachLine(Int)((String, Int) => Unit) method should" - {

        "close the input stream after use" in new InputStreamFixture{
          __Exercise__
          input.eachLine(1){ (_, _) => }
          __Verify__
          input should be (closed)
        }

        "read lines one by one with line number. The first arg specifies the starting number." in
          new InputStreamFixture{
            __SetUp__
            val sut = new mutable.ListBuffer[String]
            __Exercise__
            input.eachLine(1)((s, n) => sut += s"""$n. $s""")
            __Verify__
            sut should contain theSameElementsInOrderAs List(
              "1. first line.",
              "2. second line.",
              "3. third line."
            )
          }

        "read lines one by one with line number. if the first arg omitted, line number starts at 0." in
          new InputStreamFixture {
            __SetUp__
            val sut = new mutable.ListBuffer[String]
            __Exercise__
            input.eachLine()((s, n) => sut += s"""$n. $s""")
            __Verify__
            sut should contain theSameElementsInOrderAs List(
              "0. first line.",
              "1. second line.",
              "2. third line."
            )
          }
      }

      "splitEachLine() method should" - {

        "close the input stream after use" in new InputStreamFixture{
          __Exercise__
          input.splitEachLine("\\s+".r){_ => }
          __Verify__
          input should be (closed)
        }

        "split each line by the specified regex" in new InputStreamFixture{
          __SetUp__
          val sut = new ListBuffer[String]()
          __Exercise__
          input.splitEachLine("\\s+".r)(sut ++= _)
          __Verify__
          sut should contain theSameElementsInOrderAs
            List("first", "line.", "second", "line.", "third", "line.")
        }
      }

      "filterLine(String => Boolean) method should" - {

        "close the input stream after use" in new InputStreamFixture{
          new WriterFixture{
            __Exercise__
            val writable = input.filterLine{ _ => true }
            writable.writeTo(writer)
            __Verify__
            input should be (closed)
          }

        }

        "filter line and write down to the specified writer" in new InputStreamFixture{
          new WriterFixture {
            __Exercise__
            val writable = input.filterLine(_ contains "second")
            writable.writeTo(writer)
            writer.close()
            __Verify__
            Files.readAllLines(destPath).loneElement should equal ("second line.")
          }
        }
      }

      "filterLine(Writer)(String => Boolean) method should" - {

        "close the input stream and writer after use" in new InputStreamFixture{
          new WriterFixture {
            __Exercise__
            input.filterLine(writer){ _ => true }
            __Verify__
            input should be (closed)
            writer should be (closed)
          }
        }

        "filter line and write down to the specified writer" in new InputStreamFixture{
          new WriterFixture {
            __Exercise__
            input.filterLine(writer)(_ contains "second")
            __Verify__
            Files.readAllLines(destPath).loneElement should equal ("second line.")
          }
        }
      }

      "transformLine() method should" - {

        "close the input stream and writer after use" in new InputStreamFixture{
          new WriterFixture {
            __Exercise__
            input.transformLine(writer)(_.toUpperCase)
            __Verify__
            input should be (closed)
            writer should be (closed)
          }
        }

        "transform each line and write down to the specified writer" in new InputStreamFixture{
          new WriterFixture {
            __Exercise__
            input.transformLine(writer)(_.toUpperCase)
            __Verify__
            Files.readAllLines(destPath) should contain theSameElementsInOrderAs
              List("FIRST LINE.", "SECOND LINE.", "THIRD LINE.")
          }
        }
      }

      "readLines method should" - {

        "close the input stream after use" in new InputStreamFixture{
          __Exercise__
          input.readLines
          __Verify__
          input should be (closed)
        }

        "read all lines in File" in new InputStreamFixture{
          __Exercise__
          val sut = input.readLines
          __Verify__
          sut should contain theSameElementsInOrderAs content
        }
      }
    }
  }
}
