package org.waman.gluino.nio

import java.io._
import java.nio.charset.Charset
import java.nio.file._

import org.waman.gluino.function.GluinoFunction
import org.waman.gluino.io.{FileTypeFilterProvider, FileWrapperLike}

import scala.collection.JavaConversions._

class PathWrapper(path: Path) extends FileWrapperLike[Path, PathWrapper]
    with GluinoFunction{

  override protected def getFile = path
  override def fileName: String = path.getFileName.toString

  override protected def wrap(path: Path): PathWrapper = new PathWrapper(path)
  override protected def from(s: String): Path = Paths.get(s)

  override def isFile: Boolean = Files.isRegularFile(path)
  override def isDirectory: Boolean = Files.isDirectory(path)

  def isOlderThan(arg: Path): Boolean =
    Files.getLastModifiedTime(path).toMillis < Files.getLastModifiedTime(arg).toMillis

  def isNewerThan(arg: Path): Boolean =
    Files.getLastModifiedTime(path).toMillis > Files.getLastModifiedTime(arg).toMillis

  override protected def getFileFilterProvider: FileTypeFilterProvider[Path] =
    GluinoPath.PathFileTypeFilterProvider

  //***** Path Operation *****
  def /(child: String): Path = /(Paths.get(child))
  def /(child: Path)  : Path = path.resolve(child)
  def \(child: String): Path = /(child)
  def \(child: Path)  : Path = /(child)

  //***** byte, InputStream/OutputStream *****
  override def newInputStream = Files.newInputStream(path)

  override def newOutputStream(append: Boolean = false) =
    if(append)Files.newOutputStream(path, StandardOpenOption.APPEND)
    else Files.newOutputStream(path)

  override def bytes: Array[Byte] = Files.readAllBytes(path)
  override def bytes_=(bytes: Array[Byte]): Unit = Files.write(path, bytes)

  //***** text(String), Reader/Writer *****
  override def newReader(charset: Charset): BufferedReader = Files.newBufferedReader(path, charset)

  override def newWriter(charset: Charset, append: Boolean = false): BufferedWriter =
    if(append)Files.newBufferedWriter(path, charset, StandardOpenOption.APPEND)
    else Files.newBufferedWriter(path, charset)

  override def readLines: Seq[String] = Files.readAllLines(path, defaultCharset)
  override def readLines(charset: Charset): Seq[String] = Files.readAllLines(path, charset)

  override def text_= (text: String) = Files.write(path, List(text))
  override def setText(text: String, charset: Charset) =
    Files.write(path, List(text), charset)

  //***** File Operation *****
  override def move(dest: Path, isOverride: Boolean = false): Option[IOException] = try{
    if(isOverride)
      Files.move(getFile, dest, StandardCopyOption.REPLACE_EXISTING)
    else
      Files.move(getFile, dest)
    None
  }catch{
    case ex: IOException => Some(ex)
  }

  override def copy(dest: Path, isOverride: Boolean = false): Option[IOException] = try{
    if(isOverride)
      Files.copy(getFile, dest, StandardCopyOption.REPLACE_EXISTING)
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