package org.waman.gluino.io

import java.nio.file.Files

import org.waman.gluino.{GluinoCustomSpec, ImplicitConversion}

class AppendableConverterSpec extends GluinoCustomSpec with AppendableConverter{

  "Outputtable" - {

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
        new InputStreamFixture{
      __Verify__
      noException should be thrownBy {
        convertImplicitly[Outputtable](input)
      }
    }

    "Outputtable by Array[Byte] should" - {

      "not close the output stream after outputTo()" in new OutputStreamFixture {
        __SetUp__
        val o = convertByteArrayToOutputtable("fourth line.".getBytes())
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to file content by Outputtable#outputTo()" in new OutputStreamWithContentFixture {
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

      "not close the output stream after outputTo()" in new OutputStreamFixture {
        __SetUp__
        val o = convertByteSeqToOutputtable("fourth line.".getBytes().toSeq)
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to file content by Outputtable#outputTo()" in new OutputStreamWithContentFixture {
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

      "not close the output stream after outputTo()" in new IOStreamFixture {
        __SetUp__
        val o = convertInputStreamToOutputtable(input)
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to file content by Outputtable#outputTo()" in new IOStreamWithContentFixture {
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

  "Writable" - {

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

    "Writable by String should" - {

      "not close the writer after writeTo()" in new WriterFixture {
        __SetUp__
        val w = convertStringToWritable("fourth line.")
        __Exercise__
        w.writeTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to file content by Writable#writeTo()" in new WriterWithContentFixture {
        __SetUp__
        val w = convertStringToWritable("fourth line.")
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          (content :+ "fourth line.")
      }
    }

    "Writable by BufferedReader should" - {

      "not close the writer after writeTo()" in new ReaderWriterFixture {
        __SetUp__
        val w = convertBufferedReaderToWritable(reader)
        __Exercise__
        w.writeTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to file content by Writable#writeTo()" in new ReaderWriterWithContentFixture {
        __SetUp__
        val w = convertBufferedReaderToWritable(reader)
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        Files.readAllLines(destPath) should contain theSameElementsInOrderAs
          (content ++ content)
      }
    }
  }
}
