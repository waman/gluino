package org.waman.gluino.nio

import java.nio.file.{Path, Paths}

import org.waman.gluino.ImplicitConversion
import org.waman.gluino.io.GluinoIOCustomSpec
import org.waman.gluino.nio.GluinoPath._

class GluinoPathSpec extends GluinoIOCustomSpec{

  "***** Temporal File/Directory *****" - {

    "createTempDirectory() method should" - {

      "create a temporal directory in tempDir if directory is not specified" in {
        __Exercise__
        val sut = createTempDirectory(deleteOnExit = true)
        __Verify__
        sut.getParent should be (tempDir)
      }

      "create a temporal directory in the specified directory if directory is specified" in {
        __SetUp__
        val dir = createTempDirectory(tempDir, deleteOnExit = true)
        __Exercise__
        val sut = createTempDirectory(dir, deleteOnExit = true)
        __Verify__
        sut.getParent should be (dir)
      }

      "create a temporal directory with the prefix if specified" in {
        __Exercise__
        val sut = createTempDirectory(prefix = "99999", deleteOnExit = true)
        __Verify__
        sut.getFileName.toString should startWith ("99999")
      }
    }

    "createTempFile() method should" - {

      "create a temporal file in tempDir if directory is not specified" in {
        __Exercise__
        val path = createTempFile(deleteOnExit = true)
        __Verify__
        path.getParent should be (tempDir)
      }

      "create a temporal file in the specified directory if directory is specified" in {
        __SetUp__
        val dir = createTempDirectory(deleteOnExit = true)
        __Exercise__
        val sut = createTempFile(dir, deleteOnExit = true)
        __Verify__
        sut.getParent should be (dir)
      }

      "create a temporal file with the prefix if specified" in {
        __Exercise__
        val sut = createTempFile(prefix = "99999", deleteOnExit = true)
        __Verify__
        sut.getFileName.toString should startWith ("99999")
      }

      "create a temporal file with the suffix if specified" in {
        __Exercise__
        val sut = createTempFile(suffix = ".txt", deleteOnExit = true)
        __Verify__
        sut.getFileName.toString should endWith (".txt")
      }

      "create a temporal file with the suffix '.tmp' if suffix omitted" in {
        __Exercise__
        val sut = createTempFile(deleteOnExit = true)
        __Verify__
        sut.getFileName.toString should endWith (".tmp")
      }
    }
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

      "implicitly convert a string representing a path to Path object" taggedAs ImplicitConversion ignore {
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

      "implicitly convert Seq[String] representing a path to Path object" taggedAs ImplicitConversion ignore {
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
