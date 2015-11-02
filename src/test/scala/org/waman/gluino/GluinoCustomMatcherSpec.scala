package org.waman.gluino

class GluinoCustomMatcherSpec extends GluinoCustomSpec{

  "closed BeMatcher should" - {
    "verify the reader to be closed" in new ReaderFixture {
      __Exercise__
      reader.close()
      __Verify__
      reader should be (closed)
    }

    "verify the writer to be closed" in new WriterFixture {
      __Exercise__
      writer.close()
      __Verify__
      writer should be (closed)
    }
  }
}
