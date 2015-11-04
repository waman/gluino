package org.waman.gluino.io

import java.io.InputStream

import org.waman.gluino.GluinoCustomSpec

class InputStreamWrapperSpec extends GluinoCustomSpec with GluinoIO{

  "withInputStream() method should" - {
    "close stream after use" in {
      __SetUp__
      val is = mock[InputStream]
      (is.close _).expects()
      __Exercise__
      is.withInputStream{ is => }
      __Verify__
    }
  }
}
