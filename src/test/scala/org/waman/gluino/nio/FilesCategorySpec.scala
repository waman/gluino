package org.waman.gluino.nio

import java.nio.file.{Paths, Files}
import java.time.{ZoneId, ZonedDateTime}

import org.scalatest.{FlatSpec, Matchers}
import org.waman.gluino.GluinoCustomSpec
import org.waman.gluino.io.GluinoIOCustomSpec
import scala.collection.JavaConversions._

class FilesCategorySpec extends GluinoIOCustomSpec with GluinoPath{


  //  "convertStringToPosixFilePermissionSet" should "implicitly convert a string to FileAttribute[java.util.Set[PosixFilePermission]]" in {
  //    val file = Files.createTempFile(null, null, "rwxrwxrwx")
  //    println(Files.getPosixFilePermissions(file))
  //  }

  "lastModified = " -{

    "update lastModified attribute of a file with ZonedDateTime object" in {
      __SetUp__
      val path = GluinoPath.createTempFile(deleteOnExit = true)
      val time0 = path.lastModifiedTime
      val zdt = ZonedDateTime.of(2015, 10, 1, 0, 0, 0, 0, ZoneId.systemDefault)
      __Exercise__
      path.lastModifiedTime = zdt
      __Verify__
      path.lastModifiedTime should not equal time0
    }
  }

//  "withDirectoryStream" should "iterate files in the directory" in {
//    val path = Paths.get(".")
//
//    path.withDirectoryStream{ ds =>
//      ds.map(_.toAbsolutePath).filter(_.endsWith("build.sbt")) should not be empty
//    }
//  }
//
//  "lines" should "list lines in the file as Stream" in {
//    val path = GluinoPath.createTempFile()
//    Files.write(path, Seq("first line.", "second line.", "third line."))
//
//    path.lines{ ls =>
//      ls.map(_.replaceAll(" line.", "")).mkString(":") should be ("first:second:third")
//    }
//  }
//
//  "list" should "list files and directories in the specified directory (not recurse)" in {
//    Paths.get(".").list{ paths =>
//      paths.map(_.toAbsolutePath).filter(_.endsWith("build.sbt")) should not be empty
//    }
//  }
//
//  "find" should "find files matching the specified condition" in {
//    Paths.get(".").find((path, atts) => path.getFileName.toString.endsWith(".scala")){ paths =>
//      paths should not be empty
//    }
//  }
//
//  "walk" should "walk files under the directory" in {
//    Paths.get(".").walk{ paths =>
//      paths should not be empty
//    }
//  }
}
