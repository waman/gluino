package org.waman.gluino.nio

import java.nio.file.{Path, Files}

import org.waman.gluino.io.{GluinoIO, GluinoIOCustomSpec}

import GluinoIO.{lineSeparator => sep}

class DirectoryBuilderSpec extends GluinoIOCustomSpec with GluinoPath{

  "DirectoryBuilder should" in {
    __Exercise__
    val target = new DirectoryBuilder{
      val baseDir = GluinoPath.createTempDirectory()
      dir("parentDir"){
        dir("childDir"){
          file("file1.txt")
          file("file2.txt", "Content")
        }
        file("file3.txt").withWriter{ w =>
          w.writeLine("1st line.")
          w.writeLine("2nd line.")
          w.writeLine("3rd line.")
        }
      }
    }.baseDir
    __Verify__
    target / "parentDir" should exist
    target / "parentDir" / "childDir" should exist
    target / "parentDir" / "childDir" / "file1.txt" should exist
    target / "parentDir" / "childDir" / "file2.txt" should exist
    target / "parentDir" / "file3.txt" should exist
    text(target / "parentDir" / "childDir" / "file2.txt") should equal ("Content" + sep)
    text(target / "parentDir" / "file3.txt") should equal ("1st line." + sep + "2nd line." + sep + "3rd line." + sep)
    __TearDown__

  }

  def text(path: Path): String = new String(Files.readAllBytes(path))
}
