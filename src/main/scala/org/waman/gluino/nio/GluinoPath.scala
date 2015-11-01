package org.waman.gluino.nio

import java.net.URI
import java.nio.file._
import java.nio.file.attribute._

import org.waman.gluino.io.AppendableConverter

import scala.collection.JavaConversions._
import scala.language.implicitConversions

trait GluinoPath extends AttributeConverter with AppendableConverter{

  //***** Temporal File/Directory *****
  val tempDir: Path = Paths.get(tmpdir)
  
  def createTempFile
    (dir: Path = tempDir, prefix: String = null, suffix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createTempFile(dir, prefix, suffix, attributes.toArray:_*)

  def createTempDirectory
    (dir: Path = tempDir, prefix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createTempDirectory(dir, prefix, attributes.toArray:_*)


  //***** Path Creation *****
  def path(s: String): Path = Paths.get(s)
  def path(seq: String*): Path = Paths.get(seq.head, seq.tail:_*)
  def path(uri: URI): Path = Paths.get(uri)
  implicit def convertStringToPath(s: String): Path = path(s)
  implicit def convertSeqToPath(seq: Seq[String]): Path = path(seq:_*)
  implicit def convertUriToPath(uri: URI): Path = path(uri)


  //***** Path Wrappers *****
  implicit def wrapPath(path: Path): PathWrapper = new PathWrapper(path)
  implicit def convertPathToScalaJdkPath(path: Path): ScalaJdkPath = new ScalaJdkPath(path)
  implicit def convertPathToFilesCategory(path: Path): FilesCategory = new FilesCategory(path)


  //***** Conversion of DirectoryStream to Stream *****
  implicit def convertDirectoryStreamToStream[E](directoryStream: DirectoryStream[E]): Stream[E] =
    directoryStream.iterator.toStream
}

object GluinoPath extends GluinoPath