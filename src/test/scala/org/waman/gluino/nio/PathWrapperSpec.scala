package org.waman.gluino.nio

import java.nio.file.{Path, Paths}

import org.waman.gluino.io.{WriterWrapperLikeSpec, InputStreamWrapperLikeSpec, ReaderWrapperLikeSpec}

class PathWrapperSpec
    extends ReaderWrapperLikeSpec[PathWrapper]
    with InputStreamWrapperLikeSpec[PathWrapper]
    with WriterWrapperLikeSpec[PathWrapper]
    with GluinoPath {

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

  override def newReaderWrapperLike: PathWrapper = new PathWrapper(readOnlyPath)

  override def newInputStreamWrapperLike: PathWrapper = new PathWrapper(readOnlyPath)
  override def newInputStreamWrapperLike_ISO2022: PathWrapper = new PathWrapper(readOnlyPathISO2022)

  override def newWriterWrapperLike(dest: Path): PathWrapper = new PathWrapper(dest)
}
