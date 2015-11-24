package org.waman.gluino.io

class FileFileTypeSpec extends GluinoIOCustomSpec with GluinoFile{

  trait TempFileFixture{
    val sut = createTempFile()
  }

  trait TempDirectoryFixture{
    val sut = createTempDirectory()
  }

  "FileType.Files should" - {

    "return true for a file in filter() call" in new TempFileFixture {
      __Verify__
      FileType.Files.filter(sut) should equal (true)
    }

    "return false for a directory in filter() call" in new TempDirectoryFixture {
      __Verify__
      FileType.Files.filter(sut) should equal (false)
    }
  }

  "FileType.Directories should" - {

    "return false for a file in a filter() call" in new TempFileFixture {
      __Verify__
      FileType.Directories.filter(sut) should equal (false)
    }

    "return true for a directory in a filter() call" in new TempDirectoryFixture {
      __Verify__
      FileType.Directories.filter(sut) should equal (true)
    }
  }

  "FileType.Any should" - {

    "return true for a file in a filter() call" in new TempFileFixture {
      __Verify__
      FileType.Any.filter(sut) should equal (true)
    }

    "return true for a directory in a filter() call" in new TempDirectoryFixture {
      __Verify__
      FileType.Any.filter(sut) should equal (true)
    }
  }
}