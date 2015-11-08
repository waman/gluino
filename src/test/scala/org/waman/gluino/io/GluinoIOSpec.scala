package org.waman.gluino.io

import java.io._
import java.nio.charset.{StandardCharsets, Charset}

import org.waman.gluino.{ImplicitConversion, GluinoIOCustomSpec}

class GluinoIOSpec extends GluinoIOCustomSpec with GluinoIO{

  "Constants" - {
    "lineSeparator should be a line separator of OS" in {
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

    "convertStringToCharset() method should" - {

      "implicitly convert the specified String to Charset" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Charset]("UTF-8")
        }
      }

      "convert the specified String to Charset returned by Charset#forName" in {
        __Verify__
        convertStringToCharset("UTF-8") should equal (StandardCharsets.UTF_8)
        convertStringToCharset("ISO-2022-JP") should equal (Charset.forName("ISO-2022-JP"))
      }

      "convert String 'default' to os-default Charset" in {
        __Verify__
        convertStringToCharset("default") should equal (Charset.defaultCharset)
      }
    }

    "InputStream should implicitly converted to InputStreamWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val is = mock[InputStream]
      __Verify__
      noException should be thrownBy{
        convertImplicitly[InputStreamWrapper](is)
      }
    }

    "OutputStream should implicitly converted to OutputStreamWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val os = mock[OutputStream]
      __Verify__
      noException should be thrownBy {
        convertImplicitly[OutputStreamWrapper](os)
      }
    }

    "Reader should implicitly converted to ReaderWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val reader = mock[Reader]
      __Verify__
      noException should be thrownBy {
        convertImplicitly[ReaderWrapper](reader)
      }
    }

    "Writer should implicitly converted to WriterWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val writer = mock[Writer]
      __Verify__
      noException should be thrownBy {
        convertImplicitly[WriterWrapper](writer)
      }
    }

    "PrintWriter should implicitly converted to PrintWriterWrapper" taggedAs ImplicitConversion in {
      __SetUp__
      val pw = new PrintWriter(mock[Writer])
      __Verify__
      noException should be thrownBy {
        convertImplicitly[PrintWriterWrapper](pw)
      }
    }
  }
}
