package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

class FileWrapper(file: File) extends FileWrapperLike[File, FileWrapper] {

  override protected def getFile: File = file

  override def fileName: String = file.getName

  override protected def from(s: String): File = new File(s)

  override protected def wrap(file: File): FileWrapper = new FileWrapper(file)

  override def exists: Boolean = file.exists()

  override def isFile: Boolean = file.isFile

  override def isDirectory: Boolean = file.isDirectory

  override def getParent: FileWrapper = wrap(file.getParentFile)

  override def size: Long = file.length

  def isOlderThan(arg: File): Boolean = file.lastModified < arg.lastModified

  def isNewerThan(arg: File): Boolean = file.lastModified > arg.lastModified

  override protected def newNotDirectoryException(message: String): IOException =
    new IOException(message)

  override protected def getFileFilterProvider: FileTypeFilterProvider[File] =
    GluinoFile.FileFileTypeFilterProvider

  //***** Path Operation *****
  override def /(child: String): File = new File(file.getPath + "/" + child)

  override def \(child: String): File = new File(file.getPath + "\\" + child)

  //***** byte, InputStream/OutputStream *****
  override def newInputStream(): InputStream = new FileInputStream(file)

  override def newOutputStream(append: Boolean = false): OutputStream = {
    createFileIfNotExists()
    new FileOutputStream(file, append)
  }

  //***** String(text), Reader/Writer *****
  override def newReader(charset: Charset = GluinoIO.defaultCharset): BufferedReader =
    new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))

  override def newWriter(charset: Charset = GluinoIO.defaultCharset, append: Boolean = false): BufferedWriter = {
    createFileIfNotExists()
    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset))
  }

  //***** File Operation *****
  def createFileIfNotExists(): Unit = if(!exists)createFile()

  override def createFile(): Option[IOException] = {
    val result = file.createNewFile()

    if(result) None
    else Some(new IOException(
      s"""Fail to create file:
          |\t$file""".stripMargin))
  }

  override def createDirectory(): Option[IOException] = {
    val result = file.mkdir()

    if(result) None
    else Some(new IOException(
      s"""Fail to create directory:
          |\t$file""".stripMargin))
  }

  override def renameTo(dest: File, isOverride: Boolean): Option[IOException] = {
    if(isOverride && dest.exists() && file != dest)
      dest.delete()

    val result = file.renameTo(dest)
    if (result)
      None
    else
      Some(new IOException(
        s"""Fail to rename/move the file:
           |\tfrom $file
           |\tto   $dest""".stripMargin))
  }

  override def copy(dest: File, isOverride: Boolean = false): Option[IOException] = {
    if(file == dest) return None

    if(dest.exists()) {
      if(isOverride)
        dest.delete()
      else
        return Some(new IOException("Dest file already exists: " + dest))
    }

    file match {
      case f if f.isFile =>
        try{
          withInputStream{ is =>
            wrap(dest) << is
            None
          }
        }catch{
          case ex: IOException => Some(ex)
        }

      case d if d.isDirectory =>
        val result = dest.mkdir()
        if(result)
          None
        else
          Some(new IOException())
    }
  }

  override def delete(): Option[IOException] = {
    file.delete() match {
      case true => None
      case false => Some(new IOException("Fail to delete a directory: " + file.getAbsoluteFile))
    }
  }

  override def eachFile(consumer: File => Unit): Unit = file match {
    case d if d.isDirectory =>
      d.listFiles().foreach(consumer)
    case f =>
      throw new IOException("eachFile() must be called on a directory: " + f)
  }
}
