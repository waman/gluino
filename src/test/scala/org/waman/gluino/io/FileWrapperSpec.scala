package org.waman.gluino.io

import java.io.{IOException, File}
import java.nio.file.{Files, Path}

import org.waman.gluino.nio.GluinoPath

import org.scalatest.OptionValues._
import org.waman.gluino.number.GluinoNumber

import scala.collection.mutable

trait FileWrapperLikeSpec[F, W <: FileWrapperLike[F, W]]
    extends InputStreamWrapperLikeSpec
    with OutputStreamWrapperLikeSpec[W]
    with GluinoNumber{

  protected def newFileWrapperLike(path: Path): W

  override protected def newInputStreamWrapperLike(path: Path) = newFileWrapperLike(path)
  override protected def newOutputStreamWrapperLike(path: Path) = newFileWrapperLike(path)

  trait FileWrapperLike_FileFixture extends FileFixture{
    val sut = newFileWrapperLike(path)
  }

  trait DirectoryFixture{
    val dir = GluinoPath.createTempDirectory()
  }

  trait FileWrapperLike_DirectoryFixture extends DirectoryFixture{
    val sut = newFileWrapperLike(dir)
  }

  trait DirectoryWithAFileFixture extends DirectoryFixture{
    val childPath = GluinoPath.createTempFile(dir)
  }

  trait FileWrapperLike_DirectoryWithAFileFixture extends DirectoryWithAFileFixture{
    val sut = newFileWrapperLike(dir)
  }

  trait DirectoryWithFilesFixture extends DirectoryFixture{
    val childPaths = initChildPaths(dir)
  }

  private def initChildPaths(dir: Path): List[Path] = {
    var children = new mutable.MutableList[Path]
    3 times {
      children += GluinoPath.createTempFile(dir)
    }
    children.toList
  }

  trait FileWrapperLike_DirectoryWithFilesFixture extends DirectoryWithFilesFixture{
    val sut = newFileWrapperLike(dir)
  }

  "***** File Operations *****" - {

    "delete() method should" - {

      "delete the file" in new FileWrapperLike_FileFixture{
        __Exercise__
        sut.delete()
        __Verify__
        path should not (exist)
      }

      "delete the directory" in new FileWrapperLike_DirectoryFixture {
        __Exercise__
        sut.delete()
        __Verify__
        dir should not (exist)
      }

      "RETURN an Option[IOException] if the file does not exist" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          Files.delete(path)
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [IOException])
        }

      "RETURN an Option[IOException] if the directory is not empty" in
        new FileWrapperLike_DirectoryWithAFileFixture {
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [IOException])
        }
    }
  }

  "***** File Operations through Files and/or Directory Structure *****" - {

    "eachFile() method should" - {

      "iterate "
    }
  }
}

class FileWrapperSpec extends FileWrapperLikeSpec[File, FileWrapper] with GluinoFile{

  trait FileOperationFixture{
    val file = new File("path/to/some/dir")
  }

  "/" - {
    "resolve a child path with the specified String" in new FileOperationFixture {
      __Exercise__
      val child = file / "child.txt"
      __Verify__
      child should equal(new File("path/to/some/dir/child.txt"))
    }
  }

  "\\" - {
    "resolve a child path with the specified String" in new FileOperationFixture {
      __Exercise__
      val child = file \ "child.txt"
      __Verify__
      child should equal(new File("path/to/some/dir\\child.txt"))
    }
  }

  override protected def newFileWrapperLike(path: Path) = new FileWrapper(path.toFile)
}
