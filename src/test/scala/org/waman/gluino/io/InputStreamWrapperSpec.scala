package org.waman.gluino.io

import java.io.InputStream
import java.nio.file.Files

import org.scalatest.LoneElement._
import org.waman.gluino.GluinoIOCustomSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class InputStreamWrapperSpec extends GluinoIOCustomSpec with GluinoIO{

  "withInputStream() method should" - {
    "close stream after use" in {
      __SetUp__
      val is = mock[InputStream]
      (is.close _).expects()
      __Exercise__
      is.withInputStream{ is => }
      __Verify__
    }
  }

  "********** Tests for methods from InputStreamWrapperLike **********" - {

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
