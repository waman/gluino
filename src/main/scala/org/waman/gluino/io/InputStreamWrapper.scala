package org.waman.gluino.io

import java.io.InputStream

class InputStreamWrapper(protected[io] val stream: InputStream) extends InputStreamWrapperLike{

  override protected def getInputStream: InputStream = stream
}
