package org.waman.gluino.io

import java.io.OutputStream
import java.nio.file.Files

import org.waman.gluino.GluinoCustomSpec

class OutputStreamWrapperSpec extends GluinoCustomSpec with GluinoIO{
  
  trait OutputStreamFixture extends FileFixture{
    val os = Files.newOutputStream(path)
  }

  "withOutputStream() method should" - {
    "flush and close stream after use" in {
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
