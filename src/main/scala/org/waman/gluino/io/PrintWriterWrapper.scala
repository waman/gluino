package org.waman.gluino.io

import java.io.PrintWriter

class PrintWriterWrapper(private[io] val printWriter: PrintWriter)
  extends PrintWriterWrapperLike[PrintWriterWrapper]{

  override protected def getPrintWriter: PrintWriter = printWriter
}
