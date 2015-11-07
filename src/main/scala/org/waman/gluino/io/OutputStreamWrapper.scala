package org.waman.gluino.io

import java.io.OutputStream

class OutputStreamWrapper(private[io] val outputStream: OutputStream)
    extends OutputStreamWrapperLike[OutputStreamWrapper]{

  override protected def getOutputStream: OutputStream = outputStream
}