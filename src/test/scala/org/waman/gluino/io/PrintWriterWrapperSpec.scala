package org.waman.gluino.io

import java.io.{PrintWriter, Writer}

class PrintWriterWrapperSpec extends GluinoIOCustomSpec with GluinoIO{

  "withPrintWriter() method should" - {
    "flush and 'not' close writer after use" in {
      __SetUp__
      val writer = mock[Writer]
      (writer.flush _).expects()
      val pw = new PrintWriter(writer)
      __Exercise__
      pw.withPrintWriter{ writer => }
      __Verify__
    }
  }

}
