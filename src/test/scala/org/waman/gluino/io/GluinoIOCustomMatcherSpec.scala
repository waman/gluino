package org.waman.gluino.io

import java.io.PrintWriter


class GluinoIOCustomMatcherSpec extends GluinoIOCustomSpec{

  "open/closed BeMatcher should" - {

    "InputStream, InputStreamWrapper" - {

      "verify the InputStream to be closed" in new InputStreamFixture {
        __Exercise__
        input.close()
        __Verify__
        input should be (closed)
      }

      "verify the InputStream to be opened" in new InputStreamFixture {
        __Verify__
        input should be (opened)
      }

      "verify the InputStreamWrapper to be closed" in new InputStreamFixture {
        __SetUp__
        val wrapper = InputStreamWrapper(input)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the InputStreamWrapper to be opened" in new InputStreamFixture {
        __SetUp__
        val wrapper = InputStreamWrapper(input)
        __Verify__
        wrapper should be (opened)
      }
    }

    "OutputStream, OutputStreamWrapper" - {

      "verify the OutputStream to be closed" in new OutputStreamFixture {
        __Exercise__
        output.close()
        __Verify__
        output should be (closed)
      }

      "verify the OutputStream to be opened" in new OutputStreamFixture {
        __Verify__
        output should be (opened)
      }

      "verify the OutputStreamWrapper to be closed" in new OutputStreamFixture {
        __SetUp__
        val wrapper = OutputStreamWrapper(output)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the OutputStreamWrapper to be opened" in new OutputStreamFixture {
        __SetUp__
        val wrapper = OutputStreamWrapper(output)
        __Verify__
        wrapper should be (opened)
      }
    }

    "Reader, ReaderWrapper" - {

      "verify the Reader to be closed" in new ReaderFixture {
        __Exercise__
        reader.close()
        __Verify__
        reader should be (closed)
      }

      "verify the Reader to be opened" in new ReaderFixture {
        __Verify__
        reader should be (opened)
      }

      "verify the ReaderWrapper to be closed" in new ReaderFixture {
        __SetUp__
        val wrapper = ReaderWrapper(reader)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the ReaderWrapper to be opened" in new ReaderFixture {
        __Verify__
        ReaderWrapper(reader) should be (opened)
      }
    }

    "Writer, WriterWrapper" - {

      "verify the Writer to be closed" in new WriterFixture {
        __Exercise__
        writer.close()
        __Verify__
        writer should be (closed)
      }

      "verify the Writer to be opened" in new WriterFixture {
        __Verify__
        writer should be (opened)
      }

      "verify the WriterWrapper to be closed" in new WriterFixture {
        __SetUp__
        val wrapper = WriterWrapper(writer)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the WriterWrapper to be opened" in new WriterFixture {
        __Verify__
        WriterWrapper(writer) should be (opened)
      }
    }

    "PrintWriter, PrintWriterWrapper" - {

      "verify the PrintWriter to be closed" in new WriterFixture {
        __SetUp__
        val pw = new PrintWriter(writer)
        __Exercise__
        pw.close()
        __Verify__
        pw should be (closed)
      }

      "verify the PrintWriter to be opened" in new WriterFixture {
        __SetUp__
        val pw = new PrintWriter(writer)
        __Verify__
        pw should be (opened)
      }

      "verify the PrintWriterWrapper to be closed" in new WriterFixture {
        __SetUp__
        val wrapper = PrintWriterWrapper(writer)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the PrintWriterWrapper to be opened" in new WriterFixture {
        __SetUp__
        val wrapper = PrintWriterWrapper(writer)
        __Verify__
        wrapper should be (opened)
      }
    }
  }
}
