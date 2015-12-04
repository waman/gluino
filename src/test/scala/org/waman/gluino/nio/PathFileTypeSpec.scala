package org.waman.gluino.nio

import org.waman.gluino.io.{FileType, GluinoIOCustomSpec}

class PathFileTypeSpec extends GluinoIOCustomSpec with GluinoPath{

  "FileType.Files should" - {

    "return true for a file in a filter() call" in new FileFixture {
      __Verify__
      FileType.Files.filter(path) should equal (true)
    }

    "return false for a directory in a filter() call" in new DirectoryFixture {
      __Verify__
      FileType.Files.filter(dir) should equal (false)
    }
  }

  "FileType.Directories should" - {

    "return false for a file in a filter() call" in new FileFixture {
      __Verify__
      FileType.Directories.filter(path) should equal (false)
    }

    "return true for a directory in a filter() call" in new DirectoryFixture {
      __Verify__
      FileType.Directories.filter(dir) should equal (true)
    }
  }

  "FileType.Any should" - {

    "return true for a file in a filter() call" in new FileFixture {
      __Verify__
      FileType.Any.filter(path) should equal (true)
    }

    "return true for a directory in a filter() call" in new DirectoryFixture {
      __Verify__
      FileType.Any.filter(dir) should equal (true)
    }
  }
}