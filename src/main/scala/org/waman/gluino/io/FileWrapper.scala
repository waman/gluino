package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

class FileWrapper(file: File) extends FileWrapperLike[FileWrapper]{

  //***** Path Operation *****
  def /(child: String): File = new File(file.getPath + "/" + child)
  def \(child: String): File = new File(file.getPath + "\\" + child)

  private def filenameFilter(fileName: String): FilenameFilter = new FilenameFilter {
    override def accept(file: File, s: String): Boolean = fileName == s
  }

  //***** byte, InputStream/OutputStream *****
  override def newInputStream: InputStream =
    new BufferedInputStream(new FileInputStream(file))

  override def newOutputStream(append: Boolean): OutputStream =
    new BufferedOutputStream(new FileOutputStream(file, append))

  override def bytes_=(bytes: Array[Byte]): Unit =
    newOutputStream().withOutputStream(_.write(bytes))

  //***** String(text), Reader/Writer *****
  override def newReader(charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))

  override def newWriter(charset: Charset, append: Boolean): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset))
}
