package org.waman.gluino.nio

import java.io.{BufferedReader, BufferedWriter, InputStream, OutputStream}
import java.nio.channels.SeekableByteChannel
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file._
import java.nio.file.attribute._

import org.waman.gluino.function.GluinoFunction

import scala.collection.JavaConversions._

class FilesCategory(path: Path) extends GluinoFunction{

  //***** Creation *****
  def createFile(attributes: Seq[FileAttribute[_]] = Nil): Path =
    Files.createFile(path, attributes:_*)

  def createDirectory(attributes: Seq[FileAttribute[_]] = Nil): Path =
    Files.createDirectory(path, attributes:_*)

  def createDirectories(attributes: Seq[FileAttribute[_]] = Nil): Path =
    Files.createDirectories(path, attributes:_*)

  // temp-file
  def createTempFile
    (prefix: String = null, suffix: String = null, attributes: Seq[FileAttribute[_]] = Nil): Path =
    Files.createTempFile(path, prefix, suffix, attributes:_*)

  def createTempDirectory(prefix: String = null, attributes: Seq[FileAttribute[_]] = Nil): Path =
    Files.createTempDirectory(path, prefix, attributes:_*)

  // links
  def createLink(target: Path): Path = Files.createLink(path, target)

  def createSymbolicLink(target: Path, attributes: Seq[FileAttribute[_]] = Nil) =
    Files.createSymbolicLink(path, target, attributes:_*)


  //***** Operation *****
  // exist/delete
  def exists: Boolean = Files.exists(path)
  def exists(options: Set[LinkOption]): Boolean = Files.exists(path, options.toArray:_*)
  def notExists: Boolean = Files.notExists(path)
  def	notExists(options: Set[LinkOption]): Boolean = Files.notExists(path, options.toArray:_*)
  def delete(): Unit = Files.delete(path)
  def	deleteIfExists(): Boolean = Files.deleteIfExists(path)

  // copy/move
  def copy(in: InputStream, options: Set[CopyOption] = Set()): Long = Files.copy(in, path, options.toArray:_*)
  def copy(out: OutputStream): Long = Files.copy(path, out)

  def copyFrom(source: Path, options: Set[CopyOption] = Set()): Path =
    Files.copy(source, path, options.toArray:_*)

  def copyTo(target: Path, options: Set[CopyOption] = Set()): Path =
    Files.copy(path, target, options.toArray:_*)

  def moveFrom(source: Path, options: Set[CopyOption] = Set()): Path =
    Files.move(source, path, options.toArray:_*)

  def moveTo(target: Path, options: Set[CopyOption] = Set()): Path =
    Files.move(path, target, options.toArray:_*)

  def readSymbolicLink(): Path = Files.readSymbolicLink(path)


  //***** Read/Write *****
  def	isReadable: Boolean = Files.isReadable(path)
  def	isWritable: Boolean = Files.isWritable(path)

  // Byte
  @Deprecated
  def readAllBytes(): Array[Byte] = Files.readAllBytes(path)

  /** @see java.nio.file.Files#write(Path, byte[], OpenOption*) */
  def writeBytes(bytes: Array[Byte], options: Set[OpenOption] = Set()): Path =
    Files.write(path, bytes, options.toArray:_*)

  // InputStream/OutputStream
  def newInputStream(options: Set[OpenOption]): InputStream =
    Files.newInputStream(path, options.toArray:_*)

  def	newOutputStream(options: Set[OpenOption]): OutputStream =
    Files.newOutputStream(path, options.toArray:_*)

  // ByteChannel
  def	newByteChannel
    (options: Set[OpenOption] = Set(), attributes: Seq[FileAttribute[_]] = Nil): SeekableByteChannel =
    Files.newByteChannel(path, options, attributes:_*)

  // Seq[String]
  @Deprecated
  def readAllLines(charset: Charset = UTF_8): Seq[String] =
    Files.readAllLines(path, charset)

  def write
    (lines: Seq[String], charset: Charset = UTF_8, options: Set[OpenOption] = Set()): Path =
    Files.write(path, lines, charset, options.toArray:_*)

  // BufferedReader/BufferedWriter
  def	newBufferedReader(charset: Charset = UTF_8): BufferedReader =
    Files.newBufferedReader(path, charset)

  def	newBufferedWriter
    (charset: Charset = UTF_8, options: Set[OpenOption] = Set()): BufferedWriter =
    Files.newBufferedWriter(path, charset, options.toArray:_*)

  // Stream
  def lines(consumer: Stream[String] => Unit): Unit = lines()(consumer)

  def	lines(charset: Charset = UTF_8)(consumer: Stream[String] => Unit): Unit = {
    val lines = Files.lines(path, charset)
    try{
      consumer(lines)
    }finally{
      if(lines != null)
        lines.close()
    }
  }

  //***** Attributes *****
  def isRegularFile: Boolean = Files.isRegularFile(path)
  def isRegularFile(options: Set[LinkOption]): Boolean = Files.isRegularFile(path, options.toArray:_*)
  def isDirectory: Boolean = Files.isDirectory(path)
  def	isDirectory(options: Set[LinkOption]): Boolean = Files.isDirectory(path, options.toArray:_*)

  def isExecutable: Boolean = Files.isExecutable(path)
  def isHidden: Boolean = Files.isHidden(path)
  def isSameFile(path2: Path): Boolean = Files.isSameFile(path, path2)
  def isSymbolicLink: Boolean = Files.isSymbolicLink(path)

  def size: Long = Files.size(path)

  // lastModified
  def lastModifiedTime: FileTime = Files.getLastModifiedTime(path)
  def getLastModifiedTime(options: Set[LinkOption] = Set()): FileTime = Files.getLastModifiedTime(path)
  def lastModifiedTime_= (fileTime: FileTime): Path = Files.setLastModifiedTime(path, fileTime)
  def lastModifiedTime_= (time: Long): Path = lastModifiedTime_=(FileTime.fromMillis(time))

  // Owner
  def owner: UserPrincipal = Files.getOwner(path)
  def	getOwner(options: Set[LinkOption] = Set()): UserPrincipal = Files.getOwner(path, options.toArray:_*)
  def owner_=(owner: UserPrincipal): Path = Files.setOwner(path, owner)
  def owner_=(owner: String): Path =
    Files.setOwner(path, FileSystems.getDefault.getUserPrincipalLookupService.lookupPrincipalByName(owner))
  def groupOwner_=(group: String): Path =
    Files.setOwner(path, FileSystems.getDefault.getUserPrincipalLookupService.lookupPrincipalByGroupName(group))

  // Posix File Permissions
  def posixFilePermissions(options: Set[LinkOption] = Set()): Set[PosixFilePermission] =
    Files.getPosixFilePermissions(path, options.toArray:_*).toSet
  def posixFilePermissions_=(permissions: Set[PosixFilePermission]): Path =
    Files.setPosixFilePermissions(path, permissions)

  // File Attribute
  def getAttribute(attribute: String, options: Set[LinkOption] = Set()): Any =
    Files.getAttribute(path, attribute, options.toArray:_*)

  def setAttribute(attribute: String, value: Any, options: Set[LinkOption] = Set()): Path =
    Files.setAttribute(path, attribute, value, options.toArray:_*)

  def readAttributes(attributes: String, options: Set[LinkOption] = Set()): Map[String, Any] =
    mapAsScalaMap(Files.readAttributes(path, attributes, options.toArray:_*)).toMap

  /** @see readAttributes(Class, LinkOption*) */
  def readAttribute[A <: BasicFileAttributes]
    (attributeType: Class[A], options: Set[LinkOption] = Set()): A =
    Files.readAttributes(path, attributeType, options.toArray:_*)

  def getFileAttributeView[V <: FileAttributeView](attributeType: Class[V], options: Set[LinkOption] = Set()): V =
    Files.getFileAttributeView(path, attributeType, options.toArray:_*)


  //***** Directory Stream *****
  /** @see Files#newDirectoryStream() */
  def	withDirectoryStream(consumer: DirectoryStream[Path] => Unit): Unit =
    consumeDirectoryStream(Files.newDirectoryStream(path), consumer)

  def	withDirectoryStream
    (filter: DirectoryStream.Filter[_ >: Path])(consumer: DirectoryStream[Path] => Unit): Unit =
      consumeDirectoryStream(Files.newDirectoryStream(path, filter), consumer)

  def	withDirectoryStream(glob: String)(consumer: DirectoryStream[Path] => Unit): Unit =
    consumeDirectoryStream(Files.newDirectoryStream(path, glob), consumer)

  private def consumeDirectoryStream(dirStream: DirectoryStream[Path], consumer: DirectoryStream[Path] => Unit): Unit =
    try{
      consumer(dirStream)
    }finally{
      if(dirStream != null)
        dirStream.close()
    }

  def	list(consumer: Stream[Path] => Unit): Unit = {
    val list = Files.list(path)
    try{
      consumer(list)
    }finally{
      list.close()
    }
  }


  //***** walk file tree *****
  def walkFileTree
    (options: Set[FileVisitOption] = Set(), maxDepth: Int = Integer.MAX_VALUE, visitor: FileVisitor[_ >: Path]): Path =
    Files.walkFileTree(path, options, maxDepth, visitor)

  def find(matcher: (Path, BasicFileAttributes) => Boolean, maxDepth: Int = Integer.MAX_VALUE, options: Set[FileVisitOption] = Set())
          (consumer: Stream[Path] => Unit): Unit = {
    val stream = Files.find(path, maxDepth, matcher, options.toArray: _*)
    try{
      consumer(stream)
    }finally{
      if(stream != null)
        stream.close()
    }
  }

  def walk(consumer: Stream[Path] => Unit): Unit = walk()(consumer)

  def walk(maxDepth: Int = Integer.MAX_VALUE, options: Set[FileVisitOption] = Set())
          (consumer: Stream[Path] => Unit):Unit = {
    val stream = Files.walk(path, maxDepth, options.toArray: _*)
    try{
      consumer(stream)
    }finally{
      if(stream != null)
        stream.close()
    }
  }


  //***** File System etc. *****
  def probeContentType: String = Files.probeContentType(path)

  def	getFileStore: FileStore = Files.getFileStore(path)
}
