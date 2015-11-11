package org.waman.gluino.io

import java.io.File

class FileWrapperSpec
    extends ReaderWrapperLikeSpec[FileWrapper]
    with InputStreamWrapperLikeSpec[FileWrapper]
    with GluinoFile{

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

  override def newReaderWrapperLike: FileWrapper = new FileWrapper(readOnlyFile)
  override def newInputStreamWrapperLike: FileWrapper = new FileWrapper(readOnlyFile)
  override def newInputStreamWrapperLike_ISO2022: FileWrapper = new FileWrapper(readOnlyFileISO2022)


//  "********** Tests for methods from InputStreamWrapperLike trait **********" - {
//
//    "withInputStream() method should create InputStream and close after use" in {
//      __SetUp__
//      var sum = 0
//      __Exercise__
//      readOnlyFile.withInputStream{ is =>
//        var i = is.read()
//        while(i != -1){
//          sum += i
//          i = is.read()
//        }
//      }
//      __Verify__
//      sum should equal (contentAsString.sum)
//    }
//
//    "***** Bytes *****" - {
//      "eachByte() method should read bytes one by one" in {
//        __SetUp__
//        var sum = 0
//        __Exercise__
//        readOnlyFile.eachChar(b => sum += b)
//        __Verify__
//        sum should equal(contentAsString.sum)
//      }
//
//      "bytes method should return file content as Array[byte]" in {
//        __Verify__
//        readOnlyFile.bytes should be(contentAsString.getBytes())
//      }
//    }
//
//    "****** Tests for methods from ReaderWrapperLike trait with encoding *****" - {
//
//      "***** text *****" - {
//        "eachChar() method should read characters one by one" in {
//          __SetUp__
//          var sum = 0
//          __Exercise__
//          readOnlyFileISO2022.eachChar(ISO2022)(c => sum += c.asInstanceOf[Int])
//          __Verify__
//          sum should equal(contentAsStringISO2022.foldLeft(0)(_ + _.asInstanceOf[Int]))
//        }
//
//        "transformChar() method should transform each char and write down to the specified writer" in new WriterFixture {
//          __Exercise__
//          readOnlyFileISO2022.transformChar(writer, ISO2022) {
//            case c if c == '行' => '番'
//            case c => c
//          }
//          __Verify__
//          Files.readAllLines(destPath) should contain theSameElementsInOrderAs
//            List("1番目", "2番目", "3番目")
//        }
//
//        "text method should return file content as String" in {
//          __Verify__
//          readOnlyFileISO2022.text(ISO2022) should be(contentAsStringISO2022)
//        }
//      }
//
//      "***** lines *****" - {
//        "eachLines(String => Unit) method should read lines one by one" in {
//          __SetUp__
//          val sut = new mutable.ListBuffer[String]
//          __Exercise__
//          readOnlyFileISO2022.eachLine(ISO2022)(sut += _)
//          __Verify__
//          sut should contain theSameElementsInOrderAs contentISO2022
//        }
//
//        "eachLine(Int)((String, Int) => Unit) method should read lines one by one with line number. The first arg specifies the starting number." in {
//          __SetUp__
//          val sut = new mutable.ListBuffer[String]
//          __Exercise__
//          readOnlyFileISO2022.eachLine(1, ISO2022)((s, n) => sut += s"""$n. $s""")
//          __Verify__
//          sut should contain theSameElementsInOrderAs List(
//            "1. 1行目",
//            "2. 2行目",
//            "3. 3行目"
//          )
//        }
//
//        "splitEachLine() method should split each line by the specified regex" in {
//          __SetUp__
//          val sut = new ListBuffer[String]()
//          __Exercise__
//          readOnlyFileISO2022.splitEachLine("行".r, ISO2022)(sut ++= _)
//          __Verify__
//          sut should contain theSameElementsInOrderAs
//            List("1", "目", "2", "目", "3", "目")
//        }
//
//        "filterLine(String => Boolean) method should filter line and write down to the specified writer" in
//          new WriterFixture {
//            __Exercise__
//            val writable = readOnlyFileISO2022.filterLine(ISO2022)(_ contains "2")
//            writable.writeTo(writer)
//            writer.close()
//            __Verify__
//            Files.readAllLines(destPath).loneElement should equal("2行目")
//          }
//
//        "filterLine(Writer)(String => Boolean) method should filter line and write down to the specified writer" in
//          new WriterFixture {
//            __Exercise__
//            readOnlyFileISO2022.filterLine(writer, ISO2022)(_ contains "2")
//            __Verify__
//            Files.readAllLines(destPath).loneElement should equal("2行目")
//          }
//
//        "transformLine() method should transform each line and write down to the specified writer" in
//          new WriterFixture {
//            __Exercise__
//            readOnlyFileISO2022.transformLine(writer, ISO2022)(line => s"""[$line]""")
//            __Verify__
//            Files.readAllLines(destPath) should contain theSameElementsInOrderAs
//              List("[1行目]", "[2行目]", "[3行目]")
//          }
//
//        "readLines method should read all lines in File" in {
//          __Exercise__
//          val sut = readOnlyFileISO2022.readLines(ISO2022)
//          __Verify__
//          sut should contain theSameElementsInOrderAs contentISO2022
//        }
//      }
//    }
//  }
}
