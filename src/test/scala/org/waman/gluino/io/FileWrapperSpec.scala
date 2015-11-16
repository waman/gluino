package org.waman.gluino.io

import java.io.File
import java.nio.file.Path

trait FileWrapperLikeSpec[T <: FileWrapperLike[T]]
    extends InputStreamWrapperLikeSpec
    with OutputStreamWrapperLikeSpec[T]{

  protected def newFileWrapperLike(path: Path): T

  override protected def newInputStreamWrapperLike = newFileWrapperLike(readOnlyPath)
  override protected def newInputStreamWrapperLike_ISO2022 = newFileWrapperLike(readOnlyPathISO2022)

  override protected def newOutputStreamWrapperLike(dest: Path) = newFileWrapperLike(dest)
}

class FileWrapperSpec extends FileWrapperLikeSpec[FileWrapper] with GluinoFile{

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
