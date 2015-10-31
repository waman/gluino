package org.waman.gluino.nio

import java.nio.file.{Path, Paths}

import org.waman.gluino.GluinoCustomSpec

class GluinoPathSpec extends GluinoCustomSpec with GluinoPath{

  "Path Creation" - {

    "convertStringToPath" - {

      "implicitly convert a string representing a path to Path object" in {
        __Exercise__
        val path: Path = "path/to/some/file.txt"
        __Verify__
        path should equal(Paths.get("path/to/some/file.txt"))
      }
    }

    "convertSeqToPath" - {
      "implicitly convert sequence of string representing a path to Path object" in {
        __Exercise__
        val path: Path = Seq("path", "to", "some", "file.txt")
        __Verify__
        path should equal (Paths.get("path/to/some/file.txt"))
      }
    }

    //  "convertUriToPath" should "implicitly convert a string representing a path to Path object" in {
    //    val path: Path = new URI("path/to/some/file.txt")
    //
    //    path should equal (Paths.get("path/to/some/file.txt"))
    //  }

  }

  "/" - {
    "resolve the child path" in {
      __SetUp__
      val path = Paths.get("path/to/some/dir")
      __Exercise__
      val child = path / "child.txt"
      __Verify__
      child should equal (Paths.get("path/to/some/dir/child.txt"))
    }
  }
}
