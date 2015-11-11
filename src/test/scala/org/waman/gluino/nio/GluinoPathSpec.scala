package org.waman.gluino.nio

import java.nio.file.{Path, Paths}

import org.waman.gluino.ImplicitConversion
import org.waman.gluino.io.GluinoIOCustomSpec

class GluinoPathSpec extends GluinoIOCustomSpec with GluinoPath{

  "***** Temporal File/Directory *****" - {

  }

  "***** Path Creation *****" - {
    val expected = Paths.get("path/to/some/file.txt")

    "path() methods should" - {

      "convert a string representing a path to Path object" in {
        path("path/to/some/file.txt") should equal (expected)
      }

      "convert sequence of strings representing a path to Path object" in {
        path("path", "to", "some", "file.txt") should equal (expected)
      }

      "convert uri representing a path to Path object" in pending
    }

    "convertStringToPath() method should" - {

      "convert a string representing a path to Path object" in {
        __Exercise__
        val sut = convertStringToPath("path/to/some/file.txt")
        __Verify__
        sut should equal (expected)
      }

      "implicitly convert a string representing a path to Path object" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Path]("path/to/some/file.txt")
        }
      }
    }

    "convertSeqToPath() method should" - {
      "convert Seq[String] representing a path to Path object" in {
        __Exercise__
        val sut = convertSeqToPath(Seq("path", "to", "some", "file.txt"))
        __Verify__
        sut should equal (expected)
      }

      "implicitly convert Seq[String] representing a path to Path object" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[Path](Seq("path", "to", "some", "file.txt"))
        }
      }
    }

    "convertUriToPath() method should" - {
      "implicitly convert a string representing a path to Path object" in pending
    }
  }

  "***** Path Wrappers *****" - {

  }

  "***** Conversion of DirectoryStream to Stream *****" - {

  }
}
