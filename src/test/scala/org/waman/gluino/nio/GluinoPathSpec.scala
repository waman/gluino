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
}
