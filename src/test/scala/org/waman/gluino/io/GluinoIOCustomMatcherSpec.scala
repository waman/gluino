package org.waman.gluino.io


class GluinoIOCustomMatcherSpec extends GluinoIOCustomSpec{

  "closed BeMatcher should" - {

    "InputStream, InputStreamWrapper" - {

      "verify the InputStream to be closed" in new InputStreamFixture {
        __Exercise__
        input.close()
        __Verify__
        input should be (closed)
      }

      "verify the InputStream not to be closed" in new InputStreamFixture {
        __Verify__
        input should not be closed
      }

      "verify the InputStreamWrapper to be closed" in new InputStreamFixture {
        __SetUp__
        val wrapper = new InputStreamWrapper(input)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the InputStreamWrapper not to be closed" in new InputStreamFixture {
        __SetUp__
        val wrapper = new InputStreamWrapper(input)
        __Verify__
        wrapper should not be closed
      }
    }

    "OutputStream, OutputStreamWrapper" - {

      "verify the OutputStream to be closed" in new OutputStreamFixture {
        __Exercise__
        output.close()
        __Verify__
        output should be (closed)
      }

      "verify the OutputStream not to be closed" in new OutputStreamFixture {
        __Verify__
        output should not be closed
      }

      "verify the OutputStreamWrapper to be closed" in new OutputStreamFixture {
        __SetUp__
        val wrapper = new OutputStreamWrapper(output)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the OutputStreamWrapper not to be closed" in new OutputStreamFixture {
        __SetUp__
        val wrapper = new OutputStreamWrapper(output)
        __Verify__
        wrapper should not be closed
      }
    }

    "Reader, ReaderWrapper" - {

      "verify the Reader to be closed" in new ReaderFixture {
        __Exercise__
        reader.close()
        __Verify__
        reader should be (closed)
      }

      "verify the Reader not to be closed" in new ReaderFixture {
        __Verify__
        reader should not be closed
      }

      "verify the ReaderWrapper to be closed" in new ReaderFixture {
        __SetUp__
        val wrapper = ReaderWrapper(reader)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the ReaderWrapper not to be closed" in new ReaderFixture {
        __Verify__
        ReaderWrapper(reader) should not be closed
      }
    }

    "Writer, WriterWrapper" - {

      "verify the Writer to be closed" in new WriterFixture {
        __Exercise__
        writer.close()
        __Verify__
        writer should be (closed)
      }

      "verify the Writer not to be closed" in new WriterFixture {
        __Verify__
        writer should not be closed
      }

      "verify the WriterWrapper to be closed" in new WriterFixture {
        __SetUp__
        val wrapper = WriterWrapper(writer)
        __Exercise__
        wrapper.close()
        __Verify__
        wrapper should be (closed)
      }

      "verify the WriterWrapper not to be closed" in new WriterFixture {
        __Verify__
        WriterWrapper(writer) should not be closed
      }
    }
  }
}
