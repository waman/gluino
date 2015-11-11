package org.waman.gluino.io

import java.io.OutputStream

class OutputStreamWrapperSpec extends GluinoIOCustomSpec with GluinoIO{

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
