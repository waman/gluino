package org.waman.gluino.nio

import java.nio.file.{Path, Paths}

import org.waman.gluino.GluinoCustomSpec

class GluinoPathSpec extends GluinoCustomSpec with GluinoPath{

  "***** Temporal File/Directory *****" - {

  }

  "***** Path Creation *****" - {

    "path() methods should" - {

      "convert a string representing a path to Path object" in {
        path("path/to/some/file.txt") should equal(Paths.get("path/to/some/file.txt"))
      }

      "convert sequence of strings representing a path to Path object" in {
        path("path", "to", "some", "file.txt") should equal(Paths.get("path/to/some/file.txt"))
      }

      "convert uri representing a path to Path object" in pending
    }

    "convertStringToPath() method should" - {
      "implicitly convert a string representing a path to Path object" in {
        __Exercise__
        val path: Path = "path/to/some/file.txt"
        __Verify__
        path should equal(Paths.get("path/to/some/file.txt"))
      }
    }

    "convertSeqToPath() method should" - {
      "implicitly convert Seq[String] representing a path to Path object" in {
        __Exercise__
        val path: Path = Seq("path", "to", "some", "file.txt")
        __Verify__
        path should equal (Paths.get("path/to/some/file.txt"))
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
