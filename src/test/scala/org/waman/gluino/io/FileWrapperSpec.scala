package org.waman.gluino.io

import java.io.File
import java.nio.file.Files

import org.scalatest.LoneElement._
import org.waman.gluino.GluinoIOCustomSpec

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class FileWrapperSpec extends GluinoIOCustomSpec with GluinoFile{

  trait FileOperationFixture{
    val file: File = new File("path/to/some/dir")
  }

  "/" - {
    "resolve a child path with the specified String" in new FileOperationFixture {
      __Exercise__
      val child = file / "child.txt"
      __Verify__
      child should equal(new File("path/to/some/dir/child.txt"))
    }
  }

  "\\" - {
    "resolve a child path with the specified String" in new FileOperationFixture {
      __Exercise__
      val child = file \ "child.txt"
      __Verify__
      child should equal(new File("path/to/some/dir\\child.txt"))
    }
  }

  "********** Tests for methods from ReaderWrapperLike trait **********" - {

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


  "********** Tests for methods from InputStreamWrapperLike trait **********" - {

    "withInputStream() method should create InputStream and close after use" in {
      __SetUp__
      var sum = 0
      __Exercise__
      readOnlyFile.withInputStream{ is =>
        var i = is.read()
        while(i != -1){
          sum += i
          i = is.read()
        }
      }
      __Verify__
      sum should equal (contentAsString.sum)
    }

    "***** Bytes *****" - {
      "eachByte() method should read bytes one by one" in {
        __SetUp__
        var sum = 0
        __Exercise__
        readOnlyFile.eachChar(b => sum += b)
        __Verify__
        sum should equal(contentAsString.sum)
      }

      "bytes method should return file content as Array[byte]" in {
        __Verify__
        readOnlyFile.bytes should be(contentAsString.getBytes())
      }
    }

    "****** Tests for methods from ReaderWrapperLike trait with encoding *****" - {

      "***** text *****" - {
        "eachChar() method should read characters one by one" in {
          __SetUp__
          var sum = 0
          __Exercise__
          readOnlyFileISO2022.eachChar(ISO2022)(c => sum += c.asInstanceOf[Int])
          __Verify__
          sum should equal(contentAsStringISO2022.foldLeft(0)(_ + _.asInstanceOf[Int]))
        }

        "transformChar() method should transform each char and write down to the specified writer" in new WriterFixture {
          __Exercise__
          readOnlyFileISO2022.transformChar(writer, ISO2022) {
            case c if c == '行' => '番'
            case c => c
          }
          __Verify__
          Files.readAllLines(destPath) should contain theSameElementsInOrderAs
            List("1番目", "2番目", "3番目")
        }

        "text method should return file content as String" in {
          __Verify__
          readOnlyFileISO2022.text(ISO2022) should be(contentAsStringISO2022)
        }
      }

      "***** lines *****" - {
        "eachLines(String => Unit) method should read lines one by one" in {
          __SetUp__
          val sut = new mutable.ListBuffer[String]
          __Exercise__
          readOnlyFileISO2022.eachLine(ISO2022)(sut += _)
          __Verify__
          sut should contain theSameElementsInOrderAs contentISO2022
        }

        "eachLine(Int)((String, Int) => Unit) method should read lines one by one with line number. The first arg specifies the starting number." in {
          __SetUp__
          val sut = new mutable.ListBuffer[String]
          __Exercise__
          readOnlyFileISO2022.eachLine(1, ISO2022)((s, n) => sut += s"""$n. $s""")
          __Verify__
          sut should contain theSameElementsInOrderAs List(
            "1. 1行目",
            "2. 2行目",
            "3. 3行目"
          )
        }

        "splitEachLine() method should split each line by the specified regex" in {
          __SetUp__
          val sut = new ListBuffer[String]()
          __Exercise__
          readOnlyFileISO2022.splitEachLine("行".r, ISO2022)(sut ++= _)
          __Verify__
          sut should contain theSameElementsInOrderAs
            List("1", "目", "2", "目", "3", "目")
        }

        "filterLine(String => Boolean) method should filter line and write down to the specified writer" in
          new WriterFixture {
            __Exercise__
            val writable = readOnlyFileISO2022.filterLine(ISO2022)(_ contains "2")
            writable.writeTo(writer)
            writer.close()
            __Verify__
            Files.readAllLines(destPath).loneElement should equal("2行目")
          }

        "filterLine(Writer)(String => Boolean) method should filter line and write down to the specified writer" in
          new WriterFixture {
            __Exercise__
            readOnlyFileISO2022.filterLine(writer, ISO2022)(_ contains "2")
            __Verify__
            Files.readAllLines(destPath).loneElement should equal("2行目")
          }

        "transformLine() method should transform each line and write down to the specified writer" in
          new WriterFixture {
            __Exercise__
            readOnlyFileISO2022.transformLine(writer, ISO2022)(line => s"""[$line]""")
            __Verify__
            Files.readAllLines(destPath) should contain theSameElementsInOrderAs
              List("[1行目]", "[2行目]", "[3行目]")
          }

        "readLines method should read all lines in File" in {
          __Exercise__
          val sut = readOnlyFileISO2022.readLines(ISO2022)
          __Verify__
          sut should contain theSameElementsInOrderAs contentISO2022
        }
      }
    }
  }
}
