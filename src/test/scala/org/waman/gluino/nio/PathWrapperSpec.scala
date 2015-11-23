package org.waman.gluino.nio

import java.nio.file.{Path, Paths}

import org.waman.gluino.io.FileWrapperLikeSpec

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
}
