package org.waman.gluino.nio

import java.nio.file.{Path, Paths}

import org.waman.gluino.GluinoCustomSpec

class PathWrapperSpec extends GluinoCustomSpec{

  val path : PathWrapper = new PathWrapper(Paths.get("path/to/some/dir"))
  val expectedChild: Path = Paths.get("path/to/some/dir/child.txt")

  "/" - {
    "resolve a child path with the specified String" in {
      __Exercise__
      val child = path / "child.txt"
      __Verify__
      child should equal (expectedChild)
    }

    "resolve a child path with the specified Path" in {
      __Exercise__
      val child = path / Paths.get("child.txt")
      __Verify__
      child should equal (expectedChild)
    }
  }

  "\\" - {
    "resolve a child path with the specified String" in {
      __Exercise__
      val child = path \ "child.txt"
      __Verify__
      child should equal (expectedChild)
    }

    "resolve a child path with the specified Path" in {
      __Exercise__
      val child = path \ Paths.get("child.txt")
      __Verify__
      child should equal (expectedChild)
    }
  }
}
