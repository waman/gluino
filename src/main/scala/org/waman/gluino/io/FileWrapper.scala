package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

class FileWrapper(file: File) extends FileWrapperLike[File, FileWrapper]{

  override def wrap(file: File): FileWrapper = new FileWrapper(file)

  //***** Path Operation *****
  def /(child: String): File = new File(file.getPath + "/" + child)
  def \(child: String): File = new File(file.getPath + "\\" + child)

  private def fileNameFilter(fileName: String): FilenameFilter = new FilenameFilter {
    override def accept(file: File, s: String): Boolean = fileName == s
  }

  //***** byte, InputStream/OutputStream *****
  override def newInputStream: InputStream = new FileInputStream(file)

  override def newOutputStream(append: Boolean = false): OutputStream =
    new FileOutputStream(file, append)

  //***** String(text), Reader/Writer *****
  override def newReader(charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))

  override def newWriter(charset: Charset, append: Boolean): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset))

  //***** File Operation *****
  override def fileName: String = file.getName
  override def isFile: Boolean = file.isFile
  override def isDirectory: Boolean = file.isDirectory

//  override def rename(name: String): Boolean = file.renameTo(new File(name))
  override def delete(): Option[IOException] = {
    file.delete() match {
      case true => Option.empty
      case false => Some(new IOException("Fail to delete a directory: " + file.getAbsoluteFile))
    }
  }

  override protected def getFileFilterProvider: FileTypeFilterProvider[File] =
    GluinoFile.FileFileTypeFilterProvider

  override def eachFile(consumer: File => Unit): Unit = file.listFiles().foreach(consumer(_))
}
