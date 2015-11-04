package org.waman.gluino.io

import java.io._

import org.waman.gluino.{ImplicitConversion, GluinoCustomSpec}

class GluinoIOSpec extends GluinoCustomSpec with GluinoIO{

  "Constants" - {
    "line separator should be set" in {
      if(System.getProperty("os.name").toLowerCase contains "windows"){
        GluinoIO.lineSeparator should equal ("\r\n")
      }else{
        GluinoIO.lineSeparator should equal ("\n")
      }
    }

    "temp should be set" in {
      GluinoIO.tmpdir should not be null
    }
  }

  "implicit conversions" - {

    "InputStream is implicitly converted to InputStreamWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val is = mock[InputStream]
      __Verify__
      noException should be thrownBy{
        convertImplicitly[InputStreamWrapper](is)
      }
    }

    "OutputStream is implicitly converted to OutputStreamWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val os = mock[OutputStream]
      __Verify__
      noException should be thrownBy {
        convertImplicitly[OutputStreamWrapper](os)
      }
    }

    "Reader is implicitly converted to ReaderWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val reader = mock[Reader]
      __Verify__
      noException should be thrownBy {
        convertImplicitly[ReaderWrapper](reader)
      }
    }

    "Writer is implicitly converted to WriterWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val writer = mock[Writer]
      __Verify__
      noException should be thrownBy {
        convertImplicitly[WriterWrapper](writer)
      }
    }

    "PrintWriter is implicitly converted to PrintWriterWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val pw = new PrintWriter(mock[Writer])
      __Verify__
      noException should be thrownBy {
        convertImplicitly[PrintWriterWrapper](pw)
      }
    }
  }
}
