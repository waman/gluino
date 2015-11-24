package org.waman.gluino.io

import java.io.{IOException, File}
import java.nio.file.{Files, Path}

import org.waman.gluino.nio.GluinoPath

import org.scalatest.OptionValues._

trait FileWrapperLikeSpec[F, W <: FileWrapperLike[F, W]]
    extends InputStreamWrapperLikeSpec
    with OutputStreamWrapperLikeSpec[W]{

  protected def newFileWrapperLike(path: Path): W

  override protected def newInputStreamWrapperLike(path: Path) = newFileWrapperLike(path)
  override protected def newOutputStreamWrapperLike(path: Path) = newFileWrapperLike(path)

  trait FileWrapperLike_FileFixture extends DestFileFixture{
    val sut = newFileWrapperLike(destPath)
  }

  trait DestDirectoryFixture{
    val destPath = GluinoPath.createTempDirectory()
    lazy val destFile = destPath.toFile
  }

  trait FileWrapperLike_DirectoryFixture extends DestDirectoryFixture{
    val sut = newFileWrapperLike(destPath)
  }

  trait DestDirectoryWithAFileFixture extends DestDirectoryFixture{
    val childPath = GluinoPath.createTempFile(destPath)
    lazy val childFile = childPath.toFile
  }

  trait FileWrapperLike_DirectoryWithAFileFixture extends DestDirectoryWithAFileFixture{
    val sut = newFileWrapperLike(destPath)
  }

  "***** File Operations *****" - {

    "delete() method should" - {

      "delete the file" in new FileWrapperLike_FileFixture{
        __Exercise__
        sut.delete()
        __Verify__
        destPath should not (exist)
      }

      "delete the directory" in new FileWrapperLike_DirectoryFixture {
        __Exercise__
        sut.delete()
        __Verify__
        destPath should not (exist)
      }

      "RETURN an Option[IOException] if the file does not exist" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          Files.delete(destPath)
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
