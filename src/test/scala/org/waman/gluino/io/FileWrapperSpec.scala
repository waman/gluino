package org.waman.gluino.io

import java.nio.file.Files

import org.waman.gluino.GluinoIOCustomSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import org.scalatest.LoneElement._

class FileWrapperSpec extends GluinoIOCustomSpec with GluinoFile{


  "********** Tests for methods from ReaderWrapperLike **********" - {

    "withReader() method should" - {
      "create Reader and close after use" in {
        __SetUp__
        val sb = new mutable.StringBuilder
        __Exercise__
        readOnlyFile.withReader{ r =>
          var i = r.read()
          while(i != -1){
            sb += i.asInstanceOf[Char]
            i = r.read()
          }
        }
        __Verify__
        sb.toString should equal (contentAsString)
      }
    }

    "***** text *****" - {
      "eachChar() method should read characters one by one" in {
        __SetUp__
        var sum = 0
        __Exercise__
        readOnlyFile.eachChar(c => sum += c.asInstanceOf[Int])
        __Verify__
        sum should equal(contentAsString.foldLeft(0)(_ + _.asInstanceOf[Int]))
      }

      "transformChar() method should transform each char and write down to the specified writer" in
        new WriterFixture {
          __Exercise__
          readOnlyFile.transformChar(writer) {
            case v if "aeiou" contains v => Character.toUpperCase(v)
            case c => c
          }
          __Verify__
          Files.readAllLines(destPath) should contain theSameElementsInOrderAs
            List("fIrst lInE.", "sEcOnd lInE.", "thIrd lInE.")
        }

      "text method should return file content as String" in {
        __Verify__
        readOnlyFile.text should be (contentAsString)
      }
    }

    "***** lines *****" - {
      "eachLines(String => Unit) method should read lines one by one" in {
        __SetUp__
        val sut = new mutable.ListBuffer[String]
        __Exercise__
        readOnlyFile.eachLine(sut += _)
        __Verify__
        sut should contain theSameElementsInOrderAs content
      }

      "eachLine(Int)((String, Int) => Unit) method should" - {

        "read lines one by one with line number. The first arg specifies the starting number." in {
          __SetUp__
          val sut = new mutable.ListBuffer[String]
          __Exercise__
          readOnlyFile.eachLine(1)((s, n) => sut += s"""$n. $s""")
          __Verify__
          sut should contain theSameElementsInOrderAs List(
            "1. first line.",
            "2. second line.",
            "3. third line."
          )
        }

        "read lines one by one with line number. if the first arg omitted, line number starts at 0." in {
          __SetUp__
          val sut = new mutable.ListBuffer[String]
          __Exercise__
          readOnlyFile.eachLine()((s, n) => sut += s"""$n. $s""")
          __Verify__
          sut should contain theSameElementsInOrderAs List(
            "0. first line.",
            "1. second line.",
            "2. third line."
          )
        }
      }

      "splitEachLine() method should split each line by the specified regex" in {
        __SetUp__
        val sut = new ListBuffer[String]()
        __Exercise__
        readOnlyFile.splitEachLine("\\s+".r)(sut ++= _)
        __Verify__
        sut should contain theSameElementsInOrderAs
          List("first", "line.", "second", "line.", "third", "line.")
      }

      "filterLine(String => Boolean) method should filter line and write down to the specified writer" in
        new WriterFixture {
          __Exercise__
          val writable = readOnlyFile.filterLine(_ contains "second")
          writable.writeTo(writer)
          writer.close()
          __Verify__
          Files.readAllLines(destPath).loneElement should equal("second line.")
        }

      "filterLine(Writer)(String => Boolean) method should filter line and write down to the specified writer" in
        new WriterFixture {
          __Exercise__
          readOnlyFile.filterLine(writer)(_ contains "second")
          __Verify__
          Files.readAllLines(destPath).loneElement should equal("second line.")
        }

      "transformLine() method should transform each line and write down to the specified writer" in
        new WriterFixture {
          __Exercise__
          readOnlyFile.transformLine(writer)(_.toUpperCase)
          __Verify__
          Files.readAllLines(destPath) should contain theSameElementsInOrderAs
            List("FIRST LINE.", "SECOND LINE.", "THIRD LINE.")
        }

      "readLines method should read all lines in File" in {
        __Exercise__
        val sut = readOnlyFile.readLines
        __Verify__
        sut should contain theSameElementsInOrderAs content
      }
    }
  }
}
