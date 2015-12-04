package org.waman.gluino.io

import java.nio.file.Files

import org.waman.gluino.ImplicitConversion
import org.waman.gluino.nio.PathWrapper

class AppendableConverterSpec extends GluinoIOCustomSpec with AppendableConverter{

  "***** Outputtable *****" - {

    "Outputtable can be implicitly converted from some types" - {

      "Array[Byte] should be implicitly converted to Outputtable" taggedAs ImplicitConversion in {
        __SetUp__
        val bytes: Array[Byte] = "first line.".getBytes()
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](bytes)
        }
      }

      "Seq[Byte] should be implicitly converted to Outputtable" taggedAs ImplicitConversion in {
        __SetUp__
        val byteSeq: Seq[Byte] = "first line.".getBytes().toSeq
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](byteSeq)
        }
      }

      "InputStream should be implicitly converted to Outputtable" taggedAs ImplicitConversion in
        new InputStreamFixture {
          __Verify__
          noException should be thrownBy {
            convertImplicitly[Outputtable](input)
          }
        }

      "File should be implicitly converted to Outputtable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](readOnlyFile)
        }
      }

      "Path should be implicitly converted to Outputtable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](readOnlyPath)
        }
      }

      "FileWrapperLike should be implicitly converted to Outputtable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Outputtable](new PathWrapper(readOnlyPath))
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
        Files.readAllLines(path) should contain theSameElementsInOrderAs
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
        Files.readAllLines(path) should contain theSameElementsInOrderAs
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
          Files.readAllLines(path) should contain theSameElementsInOrderAs
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
        Files.readAllLines(path) should contain theSameElementsInOrderAs
          (content ++ content)
      }
    }

    "Outputtable by Path should" - {

      "NOT close the output stream after outputTo() call" in new OutputStreamFixture {
        __SetUp__
        val o = convertPathToOutputtable(readOnlyPath)
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to the OutputStream" in new OutputStreamWithContentFixture {
        __SetUp__
        val o = convertPathToOutputtable(readOnlyPath)
        __Exercise__
        o.outputTo(output)
        output.close()
        __Verify__
        Files.readAllLines(path) should contain theSameElementsInOrderAs
          (content ++ content)
      }
    }

    "Outputtable by FileWrapper should" - {

      "NOT close the output stream after outputTo() call" in new OutputStreamFixture {
        __SetUp__
        val o = convertFileWrapperToOutputtable(new FileWrapper(readOnlyFile))
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to the OutputStream" in new OutputStreamWithContentFixture {
        __SetUp__
        val o = convertFileWrapperToOutputtable(new FileWrapper(readOnlyFile))
        __Exercise__
        o.outputTo(output)
        output.close()
        __Verify__
        Files.readAllLines(path) should contain theSameElementsInOrderAs
          (content ++ content)
      }
    }

    "Outputtable by PathWrapper should" - {

      "NOT close the output stream after outputTo() call" in new OutputStreamFixture {
        __SetUp__
        val o = convertPathWrapperToOutputtable(new PathWrapper(readOnlyPath))
        __Exercise__
        o.outputTo(output)
        __Verify__
        output should be (opened)
      }

      "be appended to the OutputStream" in new OutputStreamWithContentFixture {
        __SetUp__
        val o = convertPathWrapperToOutputtable(new PathWrapper(readOnlyPath))
        __Exercise__
        o.outputTo(output)
        output.close()
        __Verify__
        Files.readAllLines(path) should contain theSameElementsInOrderAs
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

      "(File, Charset) should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable]((readOnlyFileISO2022, ISO2022))
        }
      }

      "Path should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable](readOnlyPath)
        }
      }

      "(Path, Charset) should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable]((readOnlyPathISO2022, ISO2022))
        }
      }

      "FileWrapper should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable](new FileWrapper(readOnlyFile))
        }
      }

      "(FileWrapper, Charset) should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable]((new FileWrapper(readOnlyFileISO2022), ISO2022))
        }
      }

      "PathWrapper should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable](new PathWrapper(readOnlyPath))
        }
      }

      "(PathWrapper, Charset) should be implicitly converted to Writable" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Writable]((new PathWrapper(readOnlyPathISO2022), ISO2022))
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
        text(path) should equal (contentAsString + "fourth line.")
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
          text(path) should equal (contentAsString + contentAsString)
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
        text(path) should equal (contentAsString + contentAsString)
      }
    }

    "Writable by (File, Charset) should" - {

      "NOT close the writer after writeTo() call" in {
        new WriterFixture {
          __SetUp__
          val w = convertFileToWritable((readOnlyFileISO2022, ISO2022))
          __Exercise__
          w.writeTo(writer)
          __Verify__
          writer should be (opened)
        }
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertFileToWritable((readOnlyFileISO2022, ISO2022))
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(path) should equal (contentAsString + contentAsStringISO2022)
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
        text(path) should equal (contentAsString + contentAsString)
      }
    }

    "Writable by (Path, Charset) should" - {

      "NOT close the writer after writeTo() call" in new WriterFixture {
        __SetUp__
        val w = convertPathToWritable((readOnlyPathISO2022, ISO2022))
        __Exercise__
        w.writeTo(writer)
        __Verify__
        writer should be (opened)
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertPathToWritable((readOnlyPathISO2022, ISO2022))
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(path) should equal (contentAsString + contentAsStringISO2022)
      }
    }

    "Writable by FileWrapper should" - {

      "NOT close the writer after writeTo() call" in {
        new WriterFixture {
          __SetUp__
          val w = convertFileWrapperToWritable(new FileWrapper(readOnlyFile))
          __Exercise__
          w.writeTo(writer)
          __Verify__
          writer should be (opened)
        }
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertFileWrapperToWritable(new FileWrapper(readOnlyFile))
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(path) should equal (contentAsString + contentAsString)
      }
    }

    "Writable by (FileWrapper, Charset) should" - {

      "NOT close the writer after writeTo() call" in {
        new WriterFixture {
          __SetUp__
          val w = convertFileWrapperToWritable((new FileWrapper(readOnlyFileISO2022), ISO2022))
          __Exercise__
          w.writeTo(writer)
          __Verify__
          writer should be (opened)
        }
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertFileWrapperToWritable((new FileWrapper(readOnlyFileISO2022), ISO2022))
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(path) should equal (contentAsString + contentAsStringISO2022)
      }
    }

    "Writable by PathWrapper should" - {

      "NOT close the writer after writeTo() call" in new WriterFixture {
        __SetUp__
        val w = convertPathWrapperToWritable(new PathWrapper(readOnlyPath))
        __Exercise__
        w.writeTo(writer)
        __Verify__
        writer should be (opened)
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertPathWrapperToWritable(new PathWrapper(readOnlyPath))
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(path) should equal (contentAsString + contentAsString)
      }
    }

    "Writable by (PathWrapper, Charset) should" - {

      "NOT close the writer after writeTo() call" in new WriterFixture {
        __SetUp__
        val w = convertPathWrapperToWritable((new PathWrapper(readOnlyPathISO2022), ISO2022))
        __Exercise__
        w.writeTo(writer)
        __Verify__
        writer should be (opened)
      }

      "be appended to the Writer" in new WriterWithContentFixture {
        __SetUp__
        val w = convertPathWrapperToWritable((new PathWrapper(readOnlyPathISO2022), ISO2022))
        __Exercise__
        w.writeTo(writer)
        writer.close()
        __Verify__
        text(path) should equal (contentAsString + contentAsStringISO2022)
      }
    }
  }
}
