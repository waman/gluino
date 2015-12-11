package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

import scala.language.implicitConversions

trait FileWrapperLike[F, W <: FileWrapperLike[F, W]] extends GluinoIO
    with AppendableConverter
    with InputStreamWrapperLike with OutputStreamWrapperLike[W]
    with ReaderWrapperLike with WriterWrapperLike[W] with PrintWriterWrapperLike[W] {
  self: W =>

  protected def getFile: F
  protected def wrap(file: F): W
  protected def from(s: String): F

  def fileName: String

  def /(child: String): F
  def \(child: String): F

  def directorySize: Long = ???
  def length: Long = ???

  def exists: Boolean

  def isFile: Boolean
  def isDirectory: Boolean

  def isOlderThan(arg: F): Boolean
  def isNewerThan(arg: F): Boolean

  protected def getFileFilterProvider: FileTypeFilterProvider[F]

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
  def renameTo(fileName: String): Option[IOException] = renameTo(fileName, isOverride = false)
  def renameTo(fileName: String, isOverride: Boolean): Option[IOException] = renameTo(from(fileName), isOverride)

  def renameTo(dest: F): Option[IOException] = renameTo(dest, isOverride = false)

  // it is necessary to implement at least one of renameTo(F, Boolean) and move(F, Boolean)
  def renameTo(dest: F, isOverride: Boolean): Option[IOException] = move(dest, isOverride)
  def move(dest: F, isOverride: Boolean = false): Option[IOException] = renameTo(dest, isOverride)

  def copy(dest: F, isOverride: Boolean = false): Option[IOException]

  def delete(): Option[IOException]

  //***** File Operations through Files and/or Directory Structure *****
  /** eachFile[Match][Recurse], eachDir[Match][Recurse]
    * -Match --- take a file filter (F => Boolean function) to filter files
    * -Recurse --- deeply iterate directories
    * -Dir --- iterate directories only
    */
  private def toFilter(fileType: FileType): F => Boolean =
    f => fileType.filter(f)(getFileFilterProvider)

  private def toFilter(fileType: FileType, filter: F => Boolean): F => Boolean =
    f => toFilter(fileType)(f) && filter(f)

  private def consumeIfFileTypeMatches(file: F, filter: F => Boolean, consumer: F => Unit): Unit =
    if (filter(file)) consumer(file)

  // eachFiles
  def eachFile(consumer: F => Unit): Unit

  def eachFile(fileType: FileType)(consumer: F => Unit): Unit =
    doEachFileMatch(toFilter(fileType), consumer)

  def eachFileMatch(filter: F => Boolean)(consumer: F => Unit): Unit =
    doEachFileMatch(toFilter(FileType.Any, filter), consumer)

  def eachFileMatch(fileType: FileType, filter: F => Boolean)(consumer: F => Unit): Unit =
    doEachFileMatch(toFilter(fileType, filter), consumer)

  private def doEachFileMatch(filter: F => Boolean, consumer: F => Unit): Unit = eachFile { file =>
    consumeIfFileTypeMatches(file, filter, consumer)
  }

  def eachFileRecurse(fileType: FileType = FileType.Any, visitDirectoryPost: Boolean = false)
                     (consumer: F => Unit): Unit =
    doEachFileMatchRecurse(toFilter(fileType), visitDirectoryPost)(consumer)

  def eachFileMatchRecurse(fileType: FileType, filter: F => Boolean, visitDirectoryPost: Boolean = false)
                          (consumer: F => Unit): Unit =
    doEachFileMatchRecurse(toFilter(fileType, filter), visitDirectoryPost)(consumer)

  private def doEachFileMatchRecurse(filter: F => Boolean, visitDirectoryPost: Boolean = false)
                                    (consumer: F => Unit): Unit = {
    if (!visitDirectoryPost)
      consumeIfFileTypeMatches(getFile, filter, consumer)

    eachFile { f =>
      wrap(f) match {
        case file if file.isFile =>
          consumeIfFileTypeMatches(f, filter, consumer)
        case dir if dir.isDirectory =>
          dir.doEachFileMatchRecurse(filter, visitDirectoryPost)(consumer)
        case _ =>
      }
    }

    if (visitDirectoryPost)
      consumeIfFileTypeMatches(getFile, filter, consumer)
  }

  // eachDir
  def eachDir(consumer: F => Unit): Unit = eachFile(FileType.Directories)(consumer)

  def eachDirMatch(filter: F => Boolean)(consumer: F => Unit): Unit =
    eachFileMatch(FileType.Directories, filter)(consumer)

  def eachDirRecurse(consumer: F => Unit): Unit =
    eachDirRecurse(visitParentPost = false)(consumer)

  def eachDirRecurse(visitParentPost: Boolean)(consumer: F => Unit): Unit =
    eachFileRecurse(FileType.Directories, visitParentPost)(consumer)

  def eachDirMatchRecurse(filter: F => Boolean, visitParentPost: Boolean = false)
                         (consumer: F => Unit): Unit =
    eachFileMatchRecurse(FileType.Directories, filter, visitParentPost)(consumer)


  //***** Directory Operations *****
  protected def newNotDirectoryException(message: String): IOException

  private def checkResult(oex: Option[IOException]): Unit = oex match {
    case Some(x) => throw x
    case None =>
  }

  def moveDir(dest: F, isOverride: Boolean = false): Option[IOException] = {
    if(!exists)
      return None
    if(!isDirectory)
      return Some(newNotDirectoryException(
        "moveDir() method must be called on a directory: " + getFile.toString))

    @throws(classOf[IOException])
    def _moveDir(src: W, dest: F, isOverride: Boolean): Unit = {
      checkResult(src.copy(dest, isOverride))

      src.eachFile { c =>
        val child = wrap(c)
        val targetChild = wrap(dest) / child.fileName
        child match {
          case f if f.isFile =>
            checkResult(f.move(targetChild, isOverride))
          case d if d.isDirectory =>
            checkResult(d.moveDir(targetChild, isOverride))
        }
      }

      checkResult(src.delete())
    }

    try {
      _moveDir(this, dest, isOverride)
      None
    }catch{
      case ex: IOException => Some(ex)
    }
  }

  def copyDir(dest: F, isOverride: Boolean = false): Option[IOException] = {
    if(!exists)
      return None
    if(!isDirectory)
      return Some(newNotDirectoryException(
        "copyDir() method must be called on a directory: " + getFile.toString))


    @throws(classOf[IOException])
    def _copyDir(src: W, dest: F, isOverride: Boolean): Unit = {
      checkResult(src.copy(dest, isOverride))

      src.eachFile { c =>
        val child = wrap(c)
        val targetChild = wrap(dest) / child.fileName
        child match {
          case f if f.isFile =>
            checkResult(f.copy(targetChild, isOverride))
          case d if d.isDirectory =>
            checkResult(d.copyDir(targetChild, isOverride))
        }
      }
    }

    try {
      _copyDir(this, dest, isOverride)
      None
    }catch{
      case ex: IOException => Some(ex)
    }
  }

  def deleteDir(): Option[IOException] = {
    if(!exists)return None

    @throws(classOf[IOException])
    def _deleteDir(dir: W): Unit = {
      dir.eachFile { child =>
        wrap(child) match {
          case f if f.isFile =>
            checkResult(f.delete())
          case d if d.isDirectory =>
            checkResult(d.deleteDir())
        }
      }
      checkResult(dir.delete())
    }

    try {
      _deleteDir(this)
      None
    }catch{
      case ex: IOException => Some(ex)
    }
  }
}