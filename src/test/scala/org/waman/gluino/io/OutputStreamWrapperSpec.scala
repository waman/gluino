package org.waman.gluino.io

import java.io.OutputStream
import java.nio.file.Files

import org.scalamock.scalatest.MockFactory
import org.waman.gluino.GluinoCustomSpec

class OutputStreamWrapperSpec extends GluinoCustomSpec with MockFactory with GluinoIO{
  
  trait OutputStreamFixture extends TempFileFixture{
    val os = Files.newOutputStream(path)
  }

  "withOutputStream() method should" - {
    "close stream after use" in {
      __SetUp__
      val os = mock[OutputStream]
      (os.flush _).expects()
      (os.close _).expects()
      __Exercise__
      os.withOutputStream{ os => }
      __Verify__
    }
  }
}
