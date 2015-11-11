package org.waman.gluino.io

import java.nio.file.Files

class InputStreamWrapperSpec
    extends CloseableReaderWrapperLikeSpec[InputStreamWrapper]
    with CloseableInputStreamWrapperLikeSpec[InputStreamWrapper]
    with GluinoIO {

  override def newReaderWrapperLike: InputStreamWrapper =
    new InputStreamWrapper(Files.newInputStream(readOnlyPath))

  override def newInputStreamWrapperLike: InputStreamWrapper =
    new InputStreamWrapper(Files.newInputStream(readOnlyPath))

  override def newInputStreamWrapperLike_ISO2022: InputStreamWrapper =
    new InputStreamWrapper(Files.newInputStream(readOnlyPathISO2022))
}
