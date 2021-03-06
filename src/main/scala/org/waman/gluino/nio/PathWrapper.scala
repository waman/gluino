package org.waman.gluino.nio

import java.io._
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption._
import java.nio.file._
import java.nio.file.attribute.UserDefinedFileAttributeView

import org.waman.gluino.function.GluinoFunction
import org.waman.gluino.io.GluinoIO.defaultCharset
import org.waman.gluino.io.{FileTypeFilterProvider, FileWrapperLike}

import scala.collection.JavaConversions._

class PathWrapper(path: Path) extends FileWrapperLike[Path, PathWrapper]
    with GluinoFunction{

  override protected def getFile = path
  override def fileName: String = path.getFileName.toString

  override protected def wrap(path: Path): PathWrapper = new PathWrapper(path)
  override protected def from(s: String): Path = Paths.get(s)

  override def exists: Boolean = Files.exists(path)

  override def isFile: Boolean = Files.isRegularFile(path)
  override def isDirectory: Boolean = Files.isDirectory(path)

  override def getParent: PathWrapper = wrap(path.getParent)

  override def size: Long = Files.size(path)

  def isOlderThan(arg: Path): Boolean =
    Files.getLastModifiedTime(path).toMillis < Files.getLastModifiedTime(arg).toMillis

  def isNewerThan(arg: Path): Boolean =
    Files.getLastModifiedTime(path).toMillis > Files.getLastModifiedTime(arg).toMillis

  override protected def newNotDirectoryException(message: String): IOException =
    new NotDirectoryException(message)

  override protected def getFileFilterProvider: FileTypeFilterProvider[Path] =
    GluinoPath.PathFileTypeFilterProvider

  //***** Path Operation *****
  override def /(child: String): Path = /(Paths.get(child))
  def /(child: Path): Path = path.resolve(child)
  override def \(child: String): Path = /(child)
  def \(child: Path): Path = /(child)

  //***** File Attributes *****
  def getUserDefinedFileAttribute(name: String, charset: Charset = defaultCharset): String = {
    val att = Files.getFileAttributeView(path, classOf[UserDefinedFileAttributeView])
    val n = att.size(name)
    val byteBuffer = ByteBuffer.allocate(n)
    att.read(name, byteBuffer)
    new String(byteBuffer.array(), charset)
  }

  def setUserDefinedFileAttribute(name: String, value: String, charset: Charset = defaultCharset): Unit = {
    val att = Files.getFileAttributeView(path, classOf[UserDefinedFileAttributeView])
    val byteBuffer = ByteBuffer.wrap(value.getBytes(charset))
    att.write(name, byteBuffer)
  }

  //***** byte, InputStream/OutputStream *****
  override def newInputStream() = Files.newInputStream(path)

  override def newOutputStream(append: Boolean = false) =
    if(append)Files.newOutputStream(path, CREATE, APPEND)
    else Files.newOutputStream(path, CREATE)

  override def bytes: Array[Byte] = Files.readAllBytes(path)
  override def bytes_=(bytes: Array[Byte]): Unit = Files.write(path, bytes)

  //***** text(String), Reader/Writer *****
  override def newReader(charset: Charset = defaultCharset): BufferedReader =
    Files.newBufferedReader(path, charset)

  override def newWriter(charset: Charset = defaultCharset, append: Boolean = false): BufferedWriter =
    if(append)Files.newBufferedWriter(path, charset, CREATE, APPEND)
    else Files.newBufferedWriter(path, charset, CREATE)

  override def readLines: Seq[String] = Files.readAllLines(path, defaultCharset)
  override def readLines(charset: Charset): Seq[String] = Files.readAllLines(path, charset)

  override def setText(text: String, charset: Charset = defaultCharset) =
    Files.write(path, text.getBytes(charset))

  //***** File Operation *****

  override def createFile(): Option[IOException] = try{
    Files.createFile(path)
    None
  }catch{
    case ex: IOException => Some(ex)
  }

  override def createDirectory(): Option[IOException] = try{
    Files.createDirectory(path)
    None
  }catch{
    case ex: IOException => Some(ex)
  }

  override def createDirectories(): Option[IOException] = {
    if(exists && isDirectory)return None
    try{
      Files.createDirectories(path)
      None
    }catch{
      case ex: IOException => Some(ex)
    }
  }

  override def move(dest: Path, isOverride: Boolean = false): Option[IOException] = try{
    if(isOverride)
      Files.move(getFile, dest, REPLACE_EXISTING)
    else
      Files.move(getFile, dest)
    None
  }catch{
    case ex: IOException => Some(ex)
  }

  override def copy(dest: Path, isOverride: Boolean = false): Option[IOException] = try{
    if(isOverride)
      Files.copy(getFile, dest, REPLACE_EXISTING)
    else
      Files.copy(getFile, dest)
    None
  }catch{
    case ex: IOException => Some(ex)
  }

  override def delete(): Option[IOException] = {
    try{
      Files.delete(path)
      None
    }catch{
      case ex: IOException => Some(ex)
    }
  }

  //***** File *****
  override def eachFile(consumer: Path => Unit): Unit = {
    val ds = Files.list(path)
    try{
      ds.foreach(consumer)
    }finally ds.close()
  }
}