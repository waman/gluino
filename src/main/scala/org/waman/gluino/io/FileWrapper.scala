package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

class FileWrapper(file: File) extends FileWrapperLike[FileWrapper]{

  override def bytes_=(bytes: Array[Byte]): Unit =
    newOutputStream().withOutputStream(_.write(bytes))

  // new InputStream/OutputStream
  override def newInputStream: InputStream =
    new BufferedInputStream(new FileInputStream(file))

  override def newOutputStream(append: Boolean): OutputStream =
    new BufferedOutputStream(new FileOutputStream(file, append))

  // new Reader/Writer
  override def newReader(charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))

  override def newWriter(charset: Charset, append: Boolean): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset))
}
