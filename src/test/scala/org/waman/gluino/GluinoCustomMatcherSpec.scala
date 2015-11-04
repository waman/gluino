package org.waman.gluino

class GluinoCustomMatcherSpec extends GluinoCustomSpec{

  "closed BeMatcher should" - {

    "verify the input stream to be closed" in new InputStreamFixture {
      __Exercise__
      input.close()
      __Verify__
      input should be (closed)
    }

    "verify the input stream not to be closed" in new InputStreamFixture {
      __Verify__
      input should not be closed
    }

    "verify the output stream to be closed" in new OutputStreamFixture {
      __Exercise__
      output.close()
      __Verify__
      output should be (closed)
    }

    "verify the output stream not to be closed" in new OutputStreamFixture {
      __Verify__
      output should not be closed
    }

    "verify the reader to be closed" in new ReaderFixture {
      __Exercise__
      reader.close()
      __Verify__
      reader should be (closed)
    }

    "verify the reader not to be closed" in new ReaderFixture {
      __Verify__
      reader should not be closed
    }

    "verify the writer to be closed" in new WriterFixture {
      __Exercise__
      output.close()
      __Verify__
      output should be (closed)
    }

    "verify the writer not to be closed" in new WriterFixture {
      __Verify__
      output should not be closed
    }
  }
}
