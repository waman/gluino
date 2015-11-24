package org.waman.gluino.nio

import java.nio.file._

import org.waman.gluino.io.FileWrapperLikeSpec

import org.scalatest.OptionValues._

class PathWrapperSpec
    extends FileWrapperLikeSpec[Path, PathWrapper]
    with GluinoPath {

  override protected def newFileWrapperLike(path: Path): PathWrapper = new PathWrapper(path)

  trait PathOperationFixture{
    val path: Path = Paths.get("path/to/some/dir")
    lazy val expectedChild: Path = Paths.get("path/to/some/dir/child.txt")
  }

  "/" - {
    "resolve a child path with the specified String" in new PathOperationFixture {
      __Exercise__
      val child = path / "child.txt"
      __Verify__
      child should equal(expectedChild)
    }

    "resolve a child path with the specified Path" in new PathOperationFixture {
      __Exercise__
      val child = path / Paths.get("child.txt")
      __Verify__
      child should equal(expectedChild)
    }
  }

  "\\" - {
    "resolve a child path with the specified String" in new PathOperationFixture {
      __Exercise__
      val child = path \ "child.txt"
      __Verify__
      child should equal(expectedChild)
    }

    "resolve a child path with the specified Path" in new PathOperationFixture {
      __Exercise__
      val child = path \ Paths.get("child.txt")
      __Verify__
      child should equal(expectedChild)
    }
  }

  "***** File Operations for PathWrapper *****" - {

    "delete() method should" - {

      "RETURN an Option[NoSuchFileException] if the file does not exist" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          Files.delete(destPath)
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [NoSuchFileException])
        }

      "RETURN an Option[DirectoryNotEmptyException] if the directory is not empty" in
        new FileWrapperLike_DirectoryWithAFileFixture {
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [DirectoryNotEmptyException])
        }
    }
  }

}
