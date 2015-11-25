package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

import scala.language.implicitConversions

trait FileWrapperLike[F, W <: FileWrapperLike[F, W]] extends GluinoIO
    with AppendableConverter
    with InputStreamWrapperLike with OutputStreamWrapperLike[W]
    with ReaderWrapperLike with WriterWrapperLike[W] with PrintWriterWrapperLike[W]{ self: W =>

  def wrap(file: F): W

  //***** Byte, InputStream/OutputStream *****
  def newInputStream: InputStream
  def newOutputStream(append: Boolean = false): OutputStream

  override protected def getInputStream: InputStream = newInputStream
  override protected def getOutputStream: OutputStream = newOutputStream(false)

  def withOutputStreamAppend[R](consumer: OutputStream => R): R =
    newOutputStream(true).withOutputStream(consumer)

  def bytes_=(bytes: Array[Byte]): Unit =
    newOutputStream().withOutputStream(_.write(bytes))

  override def append(input: Outputtable): Unit = withOutputStreamAppend(_.append(input))

  def asWritable(charset: Charset = defaultCharset): Writable = newReader(charset)

  //***** String, Reader/Writer *****
  def newReader(charset: Charset): BufferedReader
  def newWriter(charset: Charset, append: Boolean): BufferedWriter

  override protected def getReader: BufferedReader = newReader(defaultCharset)
  override protected def getWriter: BufferedWriter = newWriter(defaultCharset, append = false)

  def withWriterAppend[R](consumer: BufferedWriter => R): R =
    withWriterAppend(defaultCharset)(consumer)

  def withWriterAppend[R](charset: Charset)(consumer: BufferedWriter => R): R =
    newWriter(charset, append = true).withWriter(consumer)

  override def text: String = super.text
  override def text(charset: Charset): String = super.text(charset)
  def text_=(text: String): Unit = setText(text, defaultCharset)
  def setText(text: String, charset: Charset) = withWriter(_.write(text))

  override def readLines: Seq[String] = super.readLines
  override def readLines(charset: Charset): Seq[String] = super.readLines(charset)

  override def append(input: Writable): Unit = withWriterAppend(input.writeTo(_))

  //***** PrintWriter *****
  def newPrintWriter(charset: Charset, append: Boolean): PrintWriter =
    new PrintWriter(newWriter(charset, append))

  def withPrintWriterAppend[R](charset: Charset)(consumer: PrintWriter => R): R =
    newPrintWriter(charset, append = true).withPrintWriter(consumer)

  //***** File Operation *****
  def fileName: String
  def isFile: Boolean
  def isDirectory: Boolean

//  def renameFileName(fileName: String): F
//
//  def rename(dest: F): Option[IOException] = move(dest)
//  def move(dest: F): Option[IOException]
//  def copy(dest: F): Option[IOException]
  def delete(): Option[IOException]

  //***** File Operations through Files and/or Directory Structure *****
  protected def getFileFilterProvider: FileTypeFilterProvider[F]

  // Files
  def eachFile(consumer: F => Unit): Unit

  def eachFile(fileType: FileType)(consumer: F => Unit): Unit =
    eachFileMatch(fileType.filter(_)(getFileFilterProvider))(consumer)

  def eachFileMatch(filter: F => Boolean)(consumer: F => Unit): Unit = eachFile{ file =>
    if(filter(file))consumer(file)
  }

  // Directories
  def eachDir(consumer: F => Unit): Unit = eachFile(FileType.Directories)(consumer)

  def eachDirMatch(filter: F => Boolean)(consumer: F => Unit): Unit = eachDir{ dir =>
    if(filter(dir))consumer(dir)
  }

  // Directory Structure
  def eachDirRecurse(consumer: F => Unit, visitDirectoryPost: Boolean = true): Unit =
    eachFileRecurse(FileType.Directories, visitDirectoryPost)(consumer)

  def eachFileRecurse(fileType: FileType = FileType.Any, visitDirectoryPost: Boolean = false)
                     (consumer: F => Unit): Unit = {
    if(!visitDirectoryPost){
      eachDir { dir =>
        if(fileType.filter(dir)(getFileFilterProvider))consumer(dir)
        wrap(dir).eachFileRecurse(fileType, visitDirectoryPost)(consumer)
      }
    }

    eachFile(fileType)(consumer)

    if(visitDirectoryPost){
      eachDir { dir =>
        wrap(dir).eachFileRecurse(fileType, visitDirectoryPost)(consumer)
        if(fileType.filter(dir)(getFileFilterProvider))consumer(dir)
      }
    }
  }
  //  def moveDir(dest: F): Option[IOException]
  //  def copyDir(dest: F): Option[IOException]
  //  def deleteDir(): Option[IOException] =
  //    try {
  //      eachFileRecurse(FileType.Any, visitDirectoryPost = true) { file =>
  //        wrap(file).delete() match {
  //          case Some(ex) => throw ex
  //          case _ =>
  //        }
  //      }
  //      Option.empty
  //    }catch {
  //      case ex: IOException => Some(ex)
  //    }
}
