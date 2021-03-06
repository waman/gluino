package org.waman.gluino.io

import java.io.InputStream
import java.nio.file.{Files, Path}

import org.scalatest.LoneElement._
import org.waman.gluino.io.datastream.{CloseableDataInputStreamWrapperLikeSpec, DataInputStreamWrapperLikeSpec}
import org.waman.gluino.io.objectstream.{CloseableObjectInputStreamWrapperLikeSpec, ObjectInputStreamWrapperLikeSpec}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

trait InputStreamWrapperLikeSpec
    extends ReaderWrapperLikeSpec
    with DataInputStreamWrapperLikeSpec
    with ObjectInputStreamWrapperLikeSpec
    with GluinoIOCustomSpec{

  protected def newInputStreamWrapperLike(path: Path): InputStreamWrapperLike

  override protected def newReaderWrapperLike(path: Path) = newInputStreamWrapperLike(path)
  override protected def newDataInputStreamWrapperLike(path: Path) = newInputStreamWrapperLike(path)
  override protected def newObjectInputStreamWrapperLike(path: Path) = newInputStreamWrapperLike(path)

  trait InputStreamWrapperLikeFixture{
    val sut = newInputStreamWrapperLike(readOnlyPath)
  }

  trait InputStreamWrapperLikeISO2022Fixture{
    val sut = newInputStreamWrapperLike(readOnlyPathISO2022)
  }

  "withInputStream() method should" - {

    "close the stream after use" in new InputStreamWrapperLikeFixture {
      __Exercise__
      val result = sut.withInputStream{ is => is }
      __Verify__
      result should be (closed)
    }

    "close the stream when exception thrown" in new InputStreamWrapperLikeFixture {
      __Exercise__
      var result: InputStream = null
      try{
        sut.withInputStream{ is =>
          result = is
          throw new RuntimeException()
        }
      }catch{
        case ex: RuntimeException =>
      }
      __Verify__
      result should be (closed)
    }

    "be able to use with the loan pattern" in new InputStreamWrapperLikeFixture {
      __SetUp__
      var sum = 0
      __Exercise__
      sut.withInputStream { is =>
        var i = is.read()
        while (i != -1) {
          sum += i
          i = is.read()
        }
      }
      __Verify__
      sum should equal(contentAsString.sum)
    }
  }

  "***** Bytes *****" - {
    "eachByte() method should read bytes one by one" in new InputStreamWrapperLikeFixture{
      __SetUp__
      var sum = 0
      __Exercise__
      sut.eachChar(b => sum += b)
      __Verify__
      sum should equal(contentAsString.sum)
    }

    "bytes method should return file content as Array[byte]" in new InputStreamWrapperLikeFixture{
      __Verify__
      sut.bytes should be (contentAsString.getBytes())
    }
  }

  "***** Tests for methods from ReaderWrapperLike trait with encoding *****" - {

    "***** text *****" - {
      "eachChar() method should read characters one by one" in new InputStreamWrapperLikeISO2022Fixture {
        __SetUp__
        var sum = 0
        __Exercise__
        sut.eachChar(ISO2022)(c => sum += c.asInstanceOf[Int])
        __Verify__
        sum should equal(contentAsStringISO2022.foldLeft(0)(_ + _.asInstanceOf[Int]))
      }

      "transformChar() method should transform each char and write down to the specified writer" in
        new InputStreamWrapperLikeISO2022Fixture {
          new WriterFixture {
            __Exercise__
            sut.transformChar(writer, ISO2022) {
              case c if c == '行' => '番'
              case c => c
            }
            __Verify__
            Files.readAllLines(path) should contain theSameElementsInOrderAs
              List("1番目", "2番目", "3番目")
          }
        }

      "text method should return file content as String" in new InputStreamWrapperLikeISO2022Fixture {
        __Verify__
        sut.text(ISO2022) should be(contentAsStringISO2022)
      }
    }

    "***** lines *****" - {
      "eachLines(String => Unit) method should read lines one by one" in
        new InputStreamWrapperLikeISO2022Fixture {
          __SetUp__
          val result = new mutable.ListBuffer[String]
          __Exercise__
          sut.eachLine(ISO2022)(result += _)
          __Verify__
          result should contain theSameElementsInOrderAs contentISO2022
        }

      """eachLine(Int)((String, Int) => Unit) method should read lines one by one with line number.
        | The first arg specifies the starting number.""".stripMargin in
        new InputStreamWrapperLikeISO2022Fixture {
          __SetUp__
          val result = new mutable.ListBuffer[String]
          __Exercise__
          sut.eachLine(1, ISO2022)((s, n) => result += s"""$n. $s""")
          __Verify__
          result should contain theSameElementsInOrderAs List(
            "1. 1行目",
            "2. 2行目",
            "3. 3行目"
          )
        }

      "splitEachLine() method should split each line by the specified regex" in
        new InputStreamWrapperLikeISO2022Fixture {
          __SetUp__
          val result = new ListBuffer[String]
          __Exercise__
          sut.splitEachLine("行".r, ISO2022)(result ++= _)
          __Verify__
          result should contain theSameElementsInOrderAs
            List("1", "目", "2", "目", "3", "目")
        }

      "filterLine(String => Boolean) method should filter line and write down to the specified writer" in
        new InputStreamWrapperLikeISO2022Fixture {
          new WriterFixture {
            __Exercise__
            val writable = sut.filterLine(ISO2022)(_ contains "2")
            writable.writeTo(writer)
            writer.close()
            __Verify__
            Files.readAllLines(path).loneElement should equal("2行目")
          }
        }

      """filterLine(Writer)(String => Boolean) method should filter line
        | and write down to the specified writer""".stripMargin in
        new InputStreamWrapperLikeISO2022Fixture {
          new WriterFixture {
            __Exercise__
            sut.filterLine(writer, ISO2022)(_ contains "2")
            __Verify__
            Files.readAllLines(path).loneElement should equal("2行目")
          }
        }

      "transformLine() method should transform each line and write down to the specified writer" in
        new InputStreamWrapperLikeISO2022Fixture {
          new WriterFixture {
            __Exercise__
            sut.transformLine(writer, ISO2022)(line => s"""[$line]""")
            __Verify__
            Files.readAllLines(path) should contain theSameElementsInOrderAs
              List("[1行目]", "[2行目]", "[3行目]")
          }
        }

      "readLines method should read all lines in File" in new InputStreamWrapperLikeISO2022Fixture {
        __Exercise__
        val result = sut.readLines(ISO2022)
        __Verify__
        result should contain theSameElementsInOrderAs contentISO2022
      }
    }
  }
}

trait CloseableInputStreamWrapperLikeSpec
    extends InputStreamWrapperLikeSpec
    with CloseableReaderWrapperLikeSpec
    with CloseableDataInputStreamWrapperLikeSpec
    with CloseableObjectInputStreamWrapperLikeSpec{

  "Methods of ReaderWrapperLike trait should properly close reader after use" - {

    "***** Bytes *****" - {

      "eachByte() method" in new InputStreamWrapperLikeFixture {
        __Exercise__
        sut.eachByte { _ => }
        __Verify__
        sut should be(closed)
      }

      "bytes method" in new InputStreamWrapperLikeFixture {
        __Exercise__
        sut.bytes
        __Verify__
        sut should be(closed)
      }
    }

    "***** Tests for methods from ReaderWrapperLike trait with encoding *****" - {

      "***** text *****" - {

        "eachChar() method" in new InputStreamWrapperLikeISO2022Fixture {
          __Exercise__
          sut.eachChar(ISO2022) { _ => }
          __Verify__
          sut should be(closed)
        }

        "transformChar() method" in new InputStreamWrapperLikeISO2022Fixture {
          new WriterFixture {
            __Exercise__
            sut.transformChar(writer, ISO2022)(c => c)
            __Verify__
            sut should be(closed)
            writer should be(closed)
          }
        }

        "text method" in new InputStreamWrapperLikeISO2022Fixture {
          __Exercise__
          sut.text(ISO2022)
          __Verify__
          sut should be(closed)
        }
      }

      "***** lines *****" - {

        "eachLines(String => Unit) method" in new InputStreamWrapperLikeISO2022Fixture {
          __Exercise__
          sut.eachLine(ISO2022) { _ => }
          __Verify__
          sut should be(closed)
        }

        "eachLine(Int)((String, Int) => Unit) method" in new InputStreamWrapperLikeISO2022Fixture {
          __Exercise__
          sut.eachLine(1, ISO2022) { (_, _) => }
          __Verify__
          sut should be(closed)
        }

        "splitEachLine() method" in new InputStreamWrapperLikeISO2022Fixture {
          __Exercise__
          sut.splitEachLine("\\s+".r, ISO2022) { _ => }
          __Verify__
          sut should be(closed)
        }

        "filterLine(String => Boolean) method" in new InputStreamWrapperLikeISO2022Fixture {
          new WriterFixture {
            __Exercise__
            val writable = sut.filterLine(ISO2022) { _ => true }
            writable.writeTo(writer)
            __Verify__
            sut should be(closed)
          }
        }

        "filterLine(Writer)(String => Boolean) method" in new InputStreamWrapperLikeISO2022Fixture {
          new WriterFixture {
            __Exercise__
            sut.filterLine(writer, ISO2022) { _ => true }
            __Verify__
            sut should be(closed)
            writer should be(closed)
          }
        }

        "transformLine() method" in new InputStreamWrapperLikeISO2022Fixture {
          new WriterFixture {
            __Exercise__
            sut.transformLine(writer, ISO2022)(_.toUpperCase)
            __Verify__
            sut should be(closed)
            writer should be(closed)
          }
        }

        "readLines method" in new InputStreamWrapperLikeISO2022Fixture {
          __Exercise__
          sut.readLines(ISO2022)
          __Verify__
          sut should be(closed)
        }
      }
    }
  }
}

class InputStreamWrapperSpec
    extends CloseableInputStreamWrapperLikeSpec{

  override protected def newInputStreamWrapperLike(path: Path) = InputStreamWrapper(path)
}
