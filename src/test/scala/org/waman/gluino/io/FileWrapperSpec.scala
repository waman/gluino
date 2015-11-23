package org.waman.gluino.io

import java.io.File
import java.nio.file.Path

trait FileWrapperLikeSpec[F, W <: FileWrapperLike[F, W]]
    extends InputStreamWrapperLikeSpec
    with OutputStreamWrapperLikeSpec[W]{

  protected def newFileWrapperLike(path: Path): W

  override protected def newInputStreamWrapperLike(path: Path) = newFileWrapperLike(path)
  override protected def newOutputStreamWrapperLike(path: Path) = newFileWrapperLike(path)

  private trait FileSUT extends DestFileFixture{
    val sut = newFileWrapperLike(destPath)
  }

  "delete() method should" - {

    "delete the file" in new FileSUT{
      __Exercise__
      sut.delete()
      __Verify__
      destPath should not (exist)
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
