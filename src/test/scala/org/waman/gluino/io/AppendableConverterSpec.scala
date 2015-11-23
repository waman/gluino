package org.waman.gluino.io

import java.nio.file.{Path, Files}

import org.waman.gluino.ImplicitConversion

class AppendableConverterSpec extends GluinoIOCustomSpec with AppendableConverter{

  "***** Outputtable *****" - {

    "Outputtable can be implicitly converted from some types" - {

      "Array[Byte] should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __SetUp__
        val bytes: Array[Byte] = "first line.".getBytes()
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](bytes)
        }
      }

      "Seq[Byte] should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __SetUp__
        val byteSeq: Seq[Byte] = "first line.".getBytes().toSeq
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](byteSeq)
        }
      }

      "InputStream should be implicitly converted to Writable" taggedAs ImplicitConversion in
        new InputStreamFixture {
          __Verify__
          noException should be thrownBy {
            convertImplicitly[Outputtable](input)
          }
        }

      "File should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](readOnlyFile)
        }
      }

      "Path should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](readOnlyPath)
        }
      }
    }

    "Outputtable by Array[Byte] should" - {

      "NOT close the OutputStream after outputTo() call" in new OutputStreamFixture {
        __SetUp__
        val o = convertByteArrayToOutputtable("fourth line.".getBytes())
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to the OutputStream" in new OutputStreamWithContentFixture {
        __SetUp__
        val o = convertByteArrayToOutputtable("fourth line.".getBytes())
        __Exercise__
        o.outputTo(output)
        output.close()
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          (content :+ "fourth line.")
      }
    }

    "Outputtable by Seq[Byte] should" - {

      "NOT close the output stream after outputTo() call" in new OutputStreamFixture {
        __SetUp__
        val o = convertByteSeqToOutputtable("fourth line.".getBytes().toSeq)
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to the OutputStream" in new OutputStreamWithContentFixture {
        __SetUp__
        val o = convertByteSeqToOutputtable("fourth line.".getBytes().toSeq)
        __Exercise__
        o.outputTo(output)
        output.close()
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          (content :+ "fourth line.")
      }
    }

    "Outputtable by InputStream should" - {

      "NOT close the output stream after outputTo() call" in new InputStreamFixture {
        new OutputStreamFixture {
          __SetUp__
          val o = convertInputStreamToOutputtable(input)
          __Exercise__
          o.outputTo(output)
          __Verify__
          output should be (opened)
        }
      }

      "be appended to the OutputStream" in new InputStreamFixture {
        new OutputStreamWithContentFixture {
          __SetUp__
          val o = convertInputStreamToOutputtable(input)
          __Exercise__
          o.outputTo(output)
          output.close()
          __Verify__
          Files.readAllLines(destPath) should contain theSameElementsInOrderAs
            (content ++ content)
        }
      }
    }

    "Outputtable by File should" - {

      "NOT close the output stream after outputTo() call" in new OutputStreamFixture {
        __SetUp__
        val o = convertFileToOutputtable(readOnlyFile)
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to the OutputStream" in new OutputStreamWithContentFixture {
        __SetUp__
        val o = convertFileToOutputtable(readOnlyFile)
        __Exercise__
        o.outputTo(output)
        output.close()
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          (content ++ content)
      }
    }

    "Outputtable by Path should" - {

      "NOT close the output stream after outputTo() call" in new OutputStreamFixture {
        __SetUp__
        val o = convertFileToOutputtable(readOnlyFile)
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to the OutputStream" in new OutputStreamWithContentFixture {
        __SetUp__
        val o = convertFileToOutputtable(readOnlyFile)
        __Exercise__
        o.outputTo(output)
        output.close()
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          (content ++ content)
      }
    }
  }

  "***** Writable *****" - {

    "can be implicitly converted from some types" - {

      "String should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable]("first line.")
        }
      }

      "BufferedReader should be implicitly converted to Writable" taggedAs
        ImplicitConversion in new ReaderFixture{
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable](reader)
        }
      }

      "File should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable](readOnlyFile)
        }
      }

      "Path should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable](readOnlyPath)
        }
      }
    }

    "Writable by String should" - {

      "NOT close the writer after writeTo() call" in new WriterFixture {
        __SetUp__
        val w = convertStringToWritable("fourth line.")
        __Exercise__
        w.writeTo(writer)
        __Verify__
        writer should be (opened)
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertStringToWritable("fourth line.")
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(destPath) should equal (contentAsString + "fourth line.")
      }
    }

    "Writable by BufferedReader should" - {

      "NOT close the writer after writeTo() call" in new ReaderFixture {
        new WriterFixture {
          __SetUp__
          val w = convertBufferedReaderToWritable(reader)
          __Exercise__
          w.writeTo(writer)
          __Verify__
          writer should be (opened)
        }
      }

      "be appended to the Writer" in new ReaderFixture {
        new WriterWithContentFixture {
          __SetUp__
          val w = convertBufferedReaderToWritable(reader)
          __Exercise__
          w.writeTo(writer)
          writer.close()
          __Verify__
          text(destPath) should equal (contentAsString + contentAsString)
        }
      }
    }

    "Writable by File should" - {

      "NOT close the writer after writeTo() call" in {
        new WriterFixture {
          __SetUp__
          val w = convertFileToWritable(readOnlyFile)
          __Exercise__
          w.writeTo(writer)
          __Verify__
          writer should be (opened)
        }
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertFileToWritable(readOnlyFile)
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(destPath) should equal (contentAsString + contentAsString)
      }
    }

    "Writable by Path should" - {

      "NOT close the writer after writeTo() call" in new WriterFixture {
        __SetUp__
        val w = convertPathToWritable(readOnlyPath)
        __Exercise__
        w.writeTo(writer)
        __Verify__
        writer should be (opened)
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertPathToWritable(readOnlyPath)
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(destPath) should equal (contentAsString + contentAsString)
      }
    }
  }

  def text(path: Path): String = new String(Files.readAllBytes(path))
}
