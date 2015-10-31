package org.waman.gluino.nio

import java.nio.file.Paths

import org.waman.gluino.GluinoCustomSpec

class PathWrapperSpec extends GluinoCustomSpec{

  trait PathFixture {
    val path = new PathWrapper(Paths.get("path/to/some/dir"))
  }

  "/" - {
    "resolve a child path with the specified String" in new PathFixture{
      __Exercise__
      val child = path / "child.txt"
      __Verify__
      child should equal (Paths.get("path/to/some/dir/child.txt"))
    }

    "resolve a child path with the specified Path" in new PathFixture {
      __Exercise__
      val child = path / Paths.get("child.txt")
      __Verify__
      child should equal (Paths.get("path/to/some/dir/child.txt"))
    }
  }

  "\\" - {
    "resolve a child path with the specified String" in new PathFixture{
      __Exercise__
      val child = path \ "child.txt"
      __Verify__
      child should equal (Paths.get("path/to/some/dir/child.txt"))
    }

    "resolve a child path with the specified Path" in new PathFixture {
      __Exercise__
      val child = path \ Paths.get("child.txt")
      __Verify__
      child should equal (Paths.get("path/to/some/dir/child.txt"))
    }
  }
}
