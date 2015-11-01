package org.waman.gluino.io

import java.io.{InputStream, FileInputStream}
import java.nio.file.Files

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.GluinoCustomSpec

class InputStreamWrapperSpec extends GluinoCustomSpec with MockFactory with GluinoIO{

  trait InputStreamFixture extends TempFileFixture{
    val is = Files.newInputStream(path)
  }

  "withInputStream() method should" - {
    "close stream after use" in{
      __SetUp__
      val is = mock[InputStream]
      (is.close _).expects()
      __Exercise__
      is.withInputStream{ is => }
      __Verify__
    }
  }
}
