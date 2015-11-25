package org.waman.gluino.nio

import org.waman.gluino.io.{FileType, GluinoIOCustomSpec}

class PathFileTypeSpec extends GluinoIOCustomSpec with GluinoPath{

  private trait SUTWithFile{
    val sut = createTempFile()
  }

  private trait SUTWithDirectory{
    val sut = createTempDirectory()
  }

  "FileType.Files should" - {

    "return true for a file in a filter() call" in new SUTWithFile {
      __Verify__
      FileType.Files.filter(sut) should equal (true)
    }

    "return false for a directory in a filter() call" in new SUTWithDirectory {
      __Verify__
      FileType.Files.filter(sut) should equal (false)
    }
  }

  "FileType.Directories should" - {

    "return false for a file in a filter() call" in new SUTWithFile {
      __Verify__
      FileType.Directories.filter(sut) should equal (false)
    }

    "return true for a directory in a filter() call" in new SUTWithDirectory {
      __Verify__
      FileType.Directories.filter(sut) should equal (true)
    }
  }

  "FileType.Any should" - {

    "return true for a file in a filter() call" in new SUTWithFile {
      __Verify__
      FileType.Any.filter(sut) should equal (true)
    }

    "return true for a directory in a filter() call" in new SUTWithDirectory {
      __Verify__
      FileType.Any.filter(sut) should equal (true)
    }
  }
}