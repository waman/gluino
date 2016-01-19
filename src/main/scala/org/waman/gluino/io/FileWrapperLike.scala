package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult._

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
  def directorySize: Long = filesRecurse(FileType.Files).map(wrap).map(_.size).sum

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

  def <<(bytes: Array[Byte]): W = <<(convertByteArrayToOutputtable(bytes))
  def <<(bytes: Seq[Byte]): W = <<(convertByteSeqToOutputtable(bytes))
  def <<(input: InputStream): W = <<(convertInputStreamToOutputtable(input))
  def <<(file: File): W = <<(convertFileToOutputtable(file))
  def <<(path: Path): W = <<(convertPathToOutputtable(path))
  def <<(f: FileWrapperLike[_, _]): W = <<(f.asOutputtable())

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

  def <<(s: String): W = <<(convertStringToWritable(s))
  def <<(reader: Reader): W = <<(convertReaderToWritable(reader))
  def <<(reader: BufferedReader): W = <<(convertBufferedReaderToWritable(reader))

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

  private def consumeFileIfMatches(file: F, filter: F => Boolean, consumer: F => Unit): Unit =
    if (filter(file)) Seq(consumer(file))
    else Nil

  // eachFiles
  def eachFile(consumer: F => Unit): Unit

  def eachFile(fileType: FileType)(consumer: F => Unit): Unit =
    doEachFileMatch(toFilter(fileType), consumer)

  def eachFileMatch(filter: F => Boolean)(consumer: F => Unit): Unit =
    doEachFileMatch(toFilter(FileType.Any, filter), consumer)

  def eachFileMatch(fileType: FileType, filter: F => Boolean)(consumer: F => Unit): Unit =
    doEachFileMatch(toFilter(fileType, filter), consumer)

  private def doEachFileMatch(filter: F => Boolean, consumer: F => Unit): Unit =
    eachFile(consumeFileIfMatches(_, filter, consumer))

  def eachFileRecurse(fileType: FileType = FileType.Any, visitDirectoryLater: Boolean = false)
                     (consumer: F => Unit): Unit =
    doEachFileMatchRecurse(toFilter(fileType), visitDirectoryLater)(consumer)

  def eachFileMatchRecurse(fileType: FileType, filter: F => Boolean, visitDirectoryLater: Boolean = false)
                          (consumer: F => Unit): Unit =
    doEachFileMatchRecurse(toFilter(fileType, filter), visitDirectoryLater)(consumer)

  private def doEachFileMatchRecurse(filter: F => Boolean, visitDirectoryLater: Boolean)
                                    (consumer: F => Unit): Unit = {
    if (!visitDirectoryLater)
      consumeFileIfMatches(getFile, filter, consumer)

    eachFile { f =>
      wrap(f) match {
        case file if file.isFile =>
          consumeFileIfMatches(f, filter, consumer)
        case dir if dir.isDirectory =>
          dir.doEachFileMatchRecurse(filter, visitDirectoryLater)(consumer)
        case _ =>
      }
    }

    if (visitDirectoryLater)
      consumeFileIfMatches(getFile, filter, consumer)
  }

  // eachDir
  def eachDir(consumer: F => Unit): Unit = eachFile(FileType.Directories)(consumer)

  def eachDirMatch(filter: F => Boolean)(consumer: F => Unit): Unit =
    eachFileMatch(FileType.Directories, filter)(consumer)

  def eachDirRecurse(consumer: F => Unit): Unit =
    eachDirRecurse(visitParentLater = false)(consumer)

  def eachDirRecurse(visitParentLater: Boolean)(consumer: F => Unit): Unit =
    eachFileRecurse(FileType.Directories, visitParentLater)(consumer)

  def eachDirMatchRecurse(filter: F => Boolean, visitParentLater: Boolean = false)
                         (consumer: F => Unit): Unit =
    eachFileMatchRecurse(FileType.Directories, filter, visitParentLater)(consumer)

  // files
  private def collect(ite: (F => Unit) => Unit): Seq[F] = {
    var result = Seq[F]()
    ite(result :+= _)
    result
  }

  def files: Seq[F] = collect(eachFile)
  def files(fileType: FileType): Seq[F] = collect(eachFile(fileType))

  def filesMatch(filter: F => Boolean): Seq[F] =
    collect(eachFileMatch(filter))

  def filesMatch(fileType: FileType, filter: F => Boolean): Seq[F] =
    collect(eachFileMatch(fileType, filter))

  def filesRecurse(fileType: FileType = FileType.Any, visitDirectoryLater: Boolean = false): Seq[F] =
    collect(eachFileRecurse(fileType, visitDirectoryLater))

  def filesMatchRecurse(fileType: FileType, filter: F => Boolean, visitDirectoryLater: Boolean = false): Seq[F] =
    collect(eachFileMatchRecurse(fileType, filter, visitDirectoryLater))

  // dirs
  def dirs: Seq[F] = collect(eachDir)

  def dirsMatch(filter: F => Boolean): Seq[F] =
    collect(eachDirMatch(filter))

  def dirsRecurse: Seq[F] = collect(eachDirRecurse)

  def dirsRecurse(visitParentLater: Boolean): Seq[F] =
    collect(eachDirRecurse(visitParentLater))

  def dirsMatchRecurse(filter: F => Boolean, visitParentLater: Boolean = false): Seq[F] =
    collect(eachDirMatchRecurse(filter, visitParentLater))

  def defaultOrderingForTraverse: (F, F) => Boolean = { (a, b) =>
    val (x, y) = (wrap(a), wrap(b))
    if(x.isFile != y.isFile)
      x.isFile < y.isFile
    else
      x.fileName < y.fileName
  }

  def traverse(fileType: FileType = FileType.Any,
               filter: F => Boolean = null,
               nameFilter: String => Boolean = null,
               excludeFilter: F => Boolean = null,
               excludeNameFilter: String => Boolean = null,

               preDir: F => FileVisitResult = null,
               postDir: F => FileVisitResult = null,

               visitRoot: Boolean = false,
               preRoot: Boolean = false,
               postRoot: Boolean = false,
               maxDepth: Int = Integer.MAX_VALUE,

               sort: (F, F) => Boolean = defaultOrderingForTraverse)
              (consumer: F => FileVisitResult): Unit = {
    // canonicalize the arguments
    val _filter: F => Boolean =
      generateFilter(fileType, filter, nameFilter, excludeFilter, excludeNameFilter)

    val _preDir: (F, Boolean) => FileVisitResult =
      if(preDir != null)
        if(preRoot) (f, isRoot) => preDir(f)
        else        (f, isRoot) => if(!isRoot) preDir(f) else CONTINUE
      else          (f, isRoot) => CONTINUE

    val _postDir: (F, Boolean) => FileVisitResult =
      if(postDir != null)
        if(postRoot) (f, isRoot) => postDir(f)
        else         (f, isRoot) => if(!isRoot)postDir(f) else CONTINUE
      else           (f, isRoot) => CONTINUE

    val _maxDepth: Int = if(maxDepth < 0) Integer.MAX_VALUE else maxDepth

    val _consumer: (F, Boolean) => FileVisitResult =
      if(visitRoot) (f, isRoot) => consumer(f)
      else          (f, isRoot) => if(!isRoot)consumer(f) else CONTINUE

    // execute traversing
    traverseDirectory(getFile, isRoot = true, _filter, _preDir, _postDir, _maxDepth, sort, _consumer)
  }

  private def generateFilter(fileType: FileType = FileType.Any,
                             filter: F => Boolean,
                             nameFilter: String => Boolean,
                             excludeFilter: F => Boolean,
                             excludeNameFilter: String => Boolean): F => Boolean = {
    var filters: Seq[F => Boolean] = Seq(toFilter(fileType))
    if(filter != null) filters :+= filter
    if(nameFilter != null) filters :+= { f: F => nameFilter(wrap(f).fileName) }
    if(excludeFilter != null) filters :+= { f: F => !excludeFilter(f) }
    if(excludeNameFilter != null) filters :+= { f: F => !excludeNameFilter(wrap(f).fileName)}

    f => filters.forall(_(f))
  }

  private def traverseDirectory(dir: F,
                                isRoot: Boolean,
                                filter: F => Boolean,
                                preDir: (F, Boolean) => FileVisitResult,
                                postDir: (F, Boolean) => FileVisitResult,
                                maxDepth: Int,
                                sort: (F, F) => Boolean,
                                consumer: (F, Boolean) => FileVisitResult): FileVisitResult = {
    var skipSubtree = if(maxDepth < 0) true else false
    var skipSibling = false

    def traverseSubtree(): FileVisitResult = {
      wrap(dir).files.sortWith(sort).toStream.map{ child =>
        val result: FileVisitResult = wrap(child) match {
          case d if d.isDirectory =>
            traverseDirectory(
              child, isRoot = false, filter, preDir, postDir,
              maxDepth-1, sort, consumer)

          case f if f.isFile =>
            if(filter(child))consumer(child, false)
            else CONTINUE
        }
        result
      }.find(result => result == TERMINATE || result == SKIP_SIBLINGS) match {
        case Some(TERMINATE) => TERMINATE
        case _ => CONTINUE
      }
    }

    //***** execute traversing *****
    // preDir
    preDir(dir, isRoot) match {
      case TERMINATE     => return TERMINATE
      case SKIP_SUBTREE  => skipSubtree = true
      case SKIP_SIBLINGS => skipSibling = true
      case _ =>
    }

    // consumer
    if(filter(dir)){
      consumer(dir, isRoot) match {
        case TERMINATE     => return TERMINATE
        case SKIP_SUBTREE  => skipSubtree = true
        case SKIP_SIBLINGS => skipSibling = true
        case _ =>
      }
    }

    // subtree
    if(!skipSubtree){
      traverseSubtree() match {
        case TERMINATE => return TERMINATE
        case _         =>
      }
    }

    // postDir
    postDir(dir, isRoot) match {
      case TERMINATE     => TERMINATE
      case SKIP_SIBLINGS => SKIP_SIBLINGS
      case _ =>
        if(skipSibling) SKIP_SIBLINGS
        else            CONTINUE
    }
  }

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

object TraverseUtil{

  implicit def convertAnyToFileVisitResult(any: Any): FileVisitResult = FileVisitResult.CONTINUE

  def continue     = FileVisitResult.CONTINUE
  def skipSiblings = FileVisitResult.SKIP_SIBLINGS
  def skipSubtree  = FileVisitResult.SKIP_SUBTREE
  def terminate    = FileVisitResult.TERMINATE
}