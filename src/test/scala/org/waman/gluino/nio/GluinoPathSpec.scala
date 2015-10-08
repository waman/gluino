package org.waman.gluino.nio

import java.nio.file.{Files, Path, Paths}
import java.time.{ZoneId, ZonedDateTime}
import org.scalatest.{FlatSpec, Matchers}
import scala.collection.JavaConversions._

class GluinoPathSpec extends FlatSpec with Matchers with GluinoPath{

  "convertStringToPath" should "implicitly convert a string representing a path to Path object" in {
    val path: Path = "path/to/some/file.txt"

    path should equal (Paths.get("path/to/some/file.txt"))
  }

  "convertSeqToPath" should "implicitly convert sequence of string representing a path to Path object" in {
    val path: Path = Seq("path", "to", "some", "file.txt")

    path should equal (Paths.get("path/to/some/file.txt"))
  }

//  "convertUriToPath" should "implicitly convert a string representing a path to Path object" in {
//    val path: Path = new URI("path/to/some/file.txt")
//
//    path should equal (Paths.get("path/to/some/file.txt"))
//  }

  "/" should "resolve the child path" in {
    //setup
    val path = Paths.get("path/to/some/dir")
    // exercise
    val child = path / "child.txt"
    // verify
    child should equal (Paths.get("path/to/some/dir/child.txt"))
  }

//  "convertStringToPosixFilePermissionSet" should "implicitly convert a string to FileAttribute[java.util.Set[PosixFilePermission]]" in {
//    val file = Files.createTempFile(null, null, "rwxrwxrwx")
//    println(Files.getPosixFilePermissions(file))
//  }

  "lastModified = " should "update lastModified attribute of a file with ZonedDateTime object" in {
    val path = Files.createTempFile(null, null)
    val time0 = path.lastModifiedTime

    val zdt = ZonedDateTime.of(2015, 10, 1, 0, 0, 0, 0, ZoneId.systemDefault)
    path.lastModifiedTime = zdt
    path.lastModifiedTime should not equal time0
  }

  "withDirectoryStream" should "iterate files in the directory" in {
    val path = Paths.get(".")

    path.withDirectoryStream{ ds =>
      ds.map(_.toAbsolutePath).filter(_.endsWith("build.sbt")) should not be empty
    }
  }

  "lines" should "list lines in the file as Stream" in {
    val path = Files.createTempFile(null, null)
    Files.write(path, Seq("first line.", "second line.", "third line."))

    path.lines{ ls =>
      ls.map(_.replaceAll(" line.", "")).mkString(":") should be ("first:second:third")
    }
  }

  "list" should "list files and directories in the specified directory (not recurse)" in {
    Paths.get(".").list{ paths =>
      paths.map(_.toAbsolutePath).filter(_.endsWith("build.sbt")) should not be empty
    }
  }

  "find" should "find files matching the specified condition" in {
    Paths.get(".").find((path, atts) => path.getFileName.toString.endsWith(".scala")){ paths =>
      paths should not be empty
    }
  }

  "walk" should "walk files under the directory" in {
    Paths.get(".").walk{ paths =>
      paths should not be empty
    }
  }
}
