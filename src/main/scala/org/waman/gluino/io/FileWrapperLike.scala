package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

import org.waman.gluino.io.GluinoIO.defaultCharset

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

  def size: Long
  def directorySize: Long = eachFileRecurse(FileType.Files)(wrap(_).size).sum

  def exists: Boolean

  def isFile: Boolean
  def isDirectory: Boolean

  def isOlderThan(arg: F): Boolean
  def isNewerThan(arg: F): Boolean

  protected def getFileFilterProvider: FileTypeFilterProvider[F]

  //***** Create *****
  def createFile(): Option[IOException]
  def createDirectory(): Option[IOException]

  //***** Byte, InputStream/OutputStream *****
  def newInputStream(): InputStream
  def newOutputStream(append: Boolean = false): OutputStream
  override protected def getInputStream: InputStream = newInputStream()
  override protected def getOutputStream: OutputStream = newOutputStream(false)

  def withOutputStreamAppend[R](consumer: OutputStream => R): R =
    newOutputStream(true).withOutputStream(consumer)

  def bytes_=(bytes: Array[Byte]): Unit = newOutputStream().withOutputStream(_.write(bytes))

  override def append(input: Outputtable): Unit = withOutputStreamAppend(_.append(input))

  def asOutputtable(): Outputtable = newInputStream()

  def <<(file: F): W = <<(wrap(file))
  def <<(wrapper: W): W = <<(wrapper.asOutputtable())

  //***** String, Reader/Writer *****
  def newReader(charset: Charset = defaultCharset): BufferedReader
  def newWriter(charset: Charset = defaultCharset, append: Boolean = false): BufferedWriter
  override protected def getReader: BufferedReader = newReader(defaultCharset)
  override protected def getWriter: BufferedWriter = newWriter(defaultCharset, append = false)

  def withWriterAppend[R](consumer: BufferedWriter => R): R =
    withWriterAppend(defaultCharset)(consumer)

  def withWriterAppend[R](charset: Charset)(consumer: BufferedWriter => R): R =
    newWriter(charset, append = true).withWriter(consumer)

  override def text: String = super.text
  override def text(charset: Charset): String = super.text(charset)
  def text_=(text: String): Unit = setText(text, defaultCharset)
  def setText(text: String, charset: Charset = defaultCharset) = withWriter(charset)(_.write(text))

  override def readLines: Seq[String] = super.readLines
  override def readLines(charset: Charset): Seq[String] = super.readLines(charset)

  override def append(input: Writable): Unit = withWriterAppend(input.writeTo(_))

  def asWritable(charset: Charset = defaultCharset): Writable = newReader(charset)

  //***** PrintWriter *****
  def newPrintWriter(charset: Charset = defaultCharset, append: Boolean = false): PrintWriter =
    new PrintWriter(newWriter(charset, append))

  def withPrintWriterAppend[R](consumer: PrintWriter => R): R = withPrintWriterAppend(defaultCharset)(consumer)

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

  private def consumeIfFileTypeMatches[R](file: F, filter: F => Boolean, consumer: F => R): Seq[R] =
    if (filter(file)) Seq(consumer(file))
    else Nil

  // eachFiles
  def eachFile[R](consumer: F => R): Seq[R]

  def eachFile[R](fileType: FileType)(consumer: F => R): Seq[R] =
    doEachFileMatch(toFilter(fileType), consumer)

  def eachFileMatch[R](filter: F => Boolean)(consumer: F => R): Seq[R] =
    doEachFileMatch(toFilter(FileType.Any, filter), consumer)

  def eachFileMatch[R](fileType: FileType, filter: F => Boolean)(consumer: F => R): Seq[R] =
    doEachFileMatch(toFilter(fileType, filter), consumer)

  private def doEachFileMatch[R](filter: F => Boolean, consumer: F => R): Seq[R] = {
    var result = Seq[R]()
    eachFile(result ++= consumeIfFileTypeMatches(_, filter, consumer))
    result
  }

  def eachFileRecurse[R](fileType: FileType = FileType.Any, visitDirectoryLater: Boolean = false)
                     (consumer: F => R): Seq[R] =
    doEachFileMatchRecurse(toFilter(fileType), visitDirectoryLater)(consumer)

  def eachFileMatchRecurse[R](fileType: FileType, filter: F => Boolean, visitDirectoryLater: Boolean = false)
                          (consumer: F => R): Seq[R] =
    doEachFileMatchRecurse(toFilter(fileType, filter), visitDirectoryLater)(consumer)

  private def doEachFileMatchRecurse[R](filter: F => Boolean, visitDirectoryLater: Boolean)
                                    (consumer: F => R): Seq[R] = {
    var result = Seq[R]()

    if (!visitDirectoryLater)
      result ++= consumeIfFileTypeMatches(getFile, filter, consumer)

    eachFile { f =>
      wrap(f) match {
        case file if file.isFile =>
          result ++= consumeIfFileTypeMatches(f, filter, consumer)
        case dir if dir.isDirectory =>
          result ++= dir.doEachFileMatchRecurse(filter, visitDirectoryLater)(consumer)
        case _ =>
      }
    }

    if (visitDirectoryLater)
      result ++= consumeIfFileTypeMatches(getFile, filter, consumer)

    result
  }

  // eachDir
  def eachDir[R](consumer: F => R): Seq[R] = eachFile(FileType.Directories)(consumer)

  def eachDirMatch[R](filter: F => Boolean)(consumer: F => R): Seq[R] =
    eachFileMatch(FileType.Directories, filter)(consumer)

  def eachDirRecurse[R](consumer: F => R): Seq[R] =
    eachDirRecurse(visitParentLater = false)(consumer)

  def eachDirRecurse[R](visitParentLater: Boolean)(consumer: F => R): Seq[R] =
    eachFileRecurse(FileType.Directories, visitParentLater)(consumer)

  def eachDirMatchRecurse[R](filter: F => Boolean, visitParentLater: Boolean = false)
                         (consumer: F => R): Seq[R] =
    eachFileMatchRecurse(FileType.Directories, filter, visitParentLater)(consumer)


  //***** Directory Operations *****
  protected def newNotDirectoryException(message: String): IOException

  private def checkResult(oex: Option[IOException]): Unit = oex match {
    case Some(x) => throw x
    case None =>
  }

  /** This method is equivalent to <code>moveDir</code>() method */
  def renameDirTo(dest: F, isOverride: Boolean = false): Option[IOException] = moveDir(dest, isOverride)

  def moveDir(dest: F, isOverride: Boolean = false): Option[IOException] = {
    if(!exists)
      return None
    if(!isDirectory)
      return Some(newNotDirectoryException(
        "moveDir()/renameDirTo() method must be called on a directory: " + getFile.toString))

    @throws(classOf[IOException])
    def _moveDir(src: W, dest: F, isOverride: Boolean): Unit = {
      checkResult(src.copy(dest, isOverride))

      src.eachFile { c =>
        val child = wrap(c)
        val targetChild = wrap(dest) / child.fileName
        child match {
          case d if d.isDirectory =>
            checkResult(d.moveDir(targetChild, isOverride))
          case f =>
            checkResult(f.move(targetChild, isOverride))
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
          case d if d.isDirectory =>
            checkResult(d.copyDir(targetChild, isOverride))
          case f =>
            checkResult(f.copy(targetChild, isOverride))
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
          case d if d.isDirectory =>
            checkResult(d.deleteDir())
          case f =>
            checkResult(f.delete())
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