package org.waman.gluino.io

class GluinoFileSpec extends GluinoIOCustomSpec with GluinoFile{

  "***** Temporal File/Directory *****" - {

    "createTempDirectory() method should" - {

      "create a temporal directory in tempDir if directory is not specified" in {
        __Exercise__
        val sut = createTempDirectory()
        __Verify__
        sut.getParentFile should be (tempDir)
      }

      "create a temporal directory in the specified directory if directory is specified" in {
        __SetUp__
        val dir = createTempDirectory(tempDir)
        __Exercise__
        val sut = createTempDirectory(dir)
        __Verify__
        sut.getParentFile should be (dir)
      }

      "create a temporal directory with the prefix if specified" in {
        __Exercise__
        val sut = createTempDirectory(prefix = "99999")
        __Verify__
        sut.getName should startWith ("99999")
      }
    }

    "createTempFile() method should" - {

      "create a temporal file in tempDir if directory is not specified" in {
        __Exercise__
        val path = createTempFile()
        __Verify__
        path.getParentFile should be (tempDir)
      }

      "create a temporal file in the specified directory if directory is specified" in {
        __SetUp__
        val dir = createTempDirectory()
        __Exercise__
        val sut = createTempFile(dir)
        __Verify__
        sut.getParentFile should be (dir)
      }

      "create a temporal file with the prefix if specified" in {
        __Exercise__
        val sut = createTempFile(prefix = "99999")
        __Verify__
        sut.getName should startWith ("99999")
      }

      "create a temporal file with the prefix 'gluino-' if prefix omitted" in {
        __Exercise__
        val sut = createTempFile()
        __Verify__
        sut.getName should startWith ("gluino-")
      }

      "create a temporal file with the suffix if specified" in {
        __Exercise__
        val sut = createTempFile(suffix = ".txt")
        __Verify__
        sut.getName should endWith (".txt")
      }

      "create a temporal file with the suffix '.tmp' if suffix omitted" in {
        __Exercise__
        val sut = createTempFile()
        __Verify__
        sut.getName should endWith (".tmp")
      }
    }
  }
}
