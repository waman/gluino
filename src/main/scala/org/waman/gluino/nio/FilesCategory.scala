package org.waman.gluino.nio

import java.io.{BufferedWriter, BufferedReader, OutputStream, InputStream}
import java.nio.channels.SeekableByteChannel
import java.nio.charset.{StandardCharsets, Charset}
import java.nio.file._
import java.nio.file.attribute._
import java.time.{OffsetDateTime, ZonedDateTime}

import scala.collection.JavaConversions
import scala.collection.JavaConversions._

class FilesCategory(path: Path){

  //***** Creation *****
  def createFile(attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createFile(path, attributes.toArray:_*)

  def createDirectory(attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createDirectory(path, attributes.toArray:_*)

  def createDirectories(attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createDirectories(path, attributes.toArray:_*)

  // temp-file
  def createTempFile
    (prefix: String = null, suffix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createTempFile(path, prefix, suffix, attributes.toArray:_*)

  def createTempDirectory(prefix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createTempDirectory(path, prefix, attributes.toArray:_*)

  // links
  def createLink(target: Path): Path = Files.createLink(path, target)

  def createSymbolicLink(target: Path, attributes: Set[FileAttribute[_]] = Set()) =
    Files.createSymbolicLink(path, target, attributes.toArray:_*)


  //***** Operation *****
  // exist/delete
  def exists(options: Set[LinkOption] = Set()): Boolean = Files.exists(path, options.toArray:_*)
  def	notExists(options: Set[LinkOption] = Set()): Boolean = Files.notExists(path, options.toArray:_*)
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

  def readSymbolicLink: Path = Files.readSymbolicLink(path)


  //***** Read/Write *****
  def	isReadable: Boolean = Files.isReadable(path)
  def	isWritable: Boolean = Files.isWritable(path)

  // Byte
  def readAllBytes: Array[Byte] = Files.readAllBytes(path)

  /** @see java.nio.file.Files#write(Path, byte[], OpenOption*) */
  def writeBytes(bytes: Array[Byte], options: Set[OpenOption] = Set()): Path =
    Files.write(path, bytes, options.toArray:_*)

  // InputStream/OutputStream
  def newInputStream(options: Set[OpenOption] = Set()): InputStream =
    Files.newInputStream(path, options.toArray:_*)

  def	newOutputStream(options: Set[OpenOption] = Set()): OutputStream =
    Files.newOutputStream(path, options.toArray:_*)

  // ByteChannel
  def	newByteChannel
    (options: Set[OpenOption] = Set(), attributes: Set[FileAttribute[_]] = Set()): SeekableByteChannel =
    Files.newByteChannel(path, options, attributes.toArray:_*)

  // Seq[String]
  def readAllLines(charset: Charset = StandardCharsets.UTF_8): Seq[String] =
    Files.readAllLines(path, charset)

  def write
    (lines: Seq[String], charset: Charset = StandardCharsets.UTF_8, options: Set[OpenOption] = Set()): Path =
    Files.write(path, lines, charset, options.toArray:_*)

  // BufferedReader/BufferedWriter
  def	newBufferedReader(charset: Charset = StandardCharsets.UTF_8): BufferedReader =
    Files.newBufferedReader(path, charset)

  def	newBufferedWriter
    (charset: Charset = StandardCharsets.UTF_8, options: Set[OpenOption] = Set()): BufferedWriter =
    Files.newBufferedWriter(path, charset, options.toArray:_*)

  //    def Stream[String]	lines(path: Path, charset: Charset = StandardCharsets.UTF_8)
  //    def Stream[Path]	list(dir: Path)
  //    def Stream[Path]	find(Path start, int maxDepth, BiPredicate[Path,BasicFileAttributes] matcher, FileVisitOption* options)


  //***** Attributes *****
  def isExecutable: Boolean = Files.isExecutable(path)
  def isHidden: Boolean = Files.isHidden(path)
  def isSameFile(path2: Path): Boolean = Files.isSameFile(path, path2)
  def	isDirectory(options: Set[LinkOption] = Set()): Boolean = Files.isDirectory(path, options.toArray:_*)
  def isRegularFile(options: Set[LinkOption] = Set()): Boolean = Files.isRegularFile(path, options.toArray:_*)
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

  def posixFilePermissions(options: Set[LinkOption] = Set()): Set[PosixFilePermission] =
    Files.getPosixFilePermissions(path, options.toArray:_*).toSet
  def posixFilePermissions_=(permissions: Set[PosixFilePermission]): Path =
    Files.setPosixFilePermissions(path, permissions)

  def getAttribute(attribute: String, options: Set[LinkOption] = Set()): Any =
    Files.getAttribute(path, attribute, options.toArray:_*)
  def setAttribute(attribute: String, value: Any, options: Set[LinkOption] = Set()): Path =
    Files.setAttribute(path, attribute, value, options.toArray:_*)
  def readAttributes(attributes: String, options: Set[LinkOption] = Set()): Map[String, Any] =
    JavaConversions.mapAsScalaMap(Files.readAttributes(path, attributes, options.toArray:_*)).toMap
  /** @see readAttributes(Class, LinkOption*) */
  def readAttribute[A <: BasicFileAttributes]
    (attributeType: Class[A], options: Set[LinkOption] = Set()): A =
    Files.readAttributes(path, attributeType, options.toArray:_*)
  def getFileAttributeView[V <: FileAttributeView](attributeType: Class[V], options: Set[LinkOption] = Set()): V =
    Files.getFileAttributeView(path, attributeType, options.toArray:_*)


  //***** Directory Stream *****
//  def withDirectoryStream(consumer: DirectoryStream[Path] => Unit) = try {
//    Files.newDirectoryStream(path).iterator().forEachRemaining(new java.util.function.Consumer[Path]() {
//      override def accept(t: Path): Unit = ???
//    })
//  }catch{
//
//  }
  //    def DirectoryStream[Path]	newDirectoryStream(dir: Path)
  //    def DirectoryStream[Path]	newDirectoryStream(dir: Path, DirectoryStream.Filter[? super Path] filter)
  //    def DirectoryStream[Path]	newDirectoryStream(dir: Path, String glob)


  //***** walk file tree *****
  def walkFileTree
    (options: Set[FileVisitOption] = Set(), maxDepth: Int = Integer.MAX_VALUE, visitor: FileVisitor[_ >: Path]): Path =
    Files.walkFileTree(path, options, maxDepth, visitor)
//  def Stream[Path]	walk(Path start, FileVisitOption* options)
//  def Stream[Path]	walk(Path start, int maxDepth, FileVisitOption* options)


  //***** File System etc. *****
  def probeContentType: String = Files.probeContentType(path)

  def	getFileStore: FileStore = Files.getFileStore(path)
}
