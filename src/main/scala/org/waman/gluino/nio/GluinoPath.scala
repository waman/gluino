package org.waman.gluino.nio

import java.net.URI
import java.nio.file._
import java.nio.file.attribute._

import org.waman.gluino.io.AppendableConverter

import scala.collection.JavaConversions._

trait GluinoPath extends AttributeConverter with AppendableConverter{

  val tmpdir: Path = Paths.get(System.getProperty("java.io.tmpdir"))
  
  def createTempFile
    (dir: Path = tmpdir, prefix: String = null, suffix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createTempFile(dir, prefix, suffix, attributes.toArray:_*)

  def createTempDirectory
    (dir: Path = tmpdir, prefix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createTempDirectory(dir, prefix, attributes.toArray:_*)


  //***** Path Creation *****
  implicit def convertStringToPath(path: String): Path = Paths.get(path)
  implicit def convertSeqToPath(path: Seq[String]): Path = Paths.get(path.head, path.tail:_*)
  implicit def convertUriToPath(uri: URI): Path = Paths.get(uri)


  //***** Path Wrappers *****
  implicit def wrapPath(path: Path): PathWrapper = new PathWrapper(path)
  implicit def convertPathToScalaJdkPath(path: Path): ScalaJdkPath = new ScalaJdkPath(path)
  implicit def convertPathToFilesCategory(path: Path): FilesCategory = new FilesCategory(path)


  //***** Conversion java.util.Stream/DirectoryStream to Stream *****
  implicit def convertJavaStreamToStream[E](stream: java.util.stream.Stream[E]): Stream[E] =
    stream.iterator.toStream

  implicit def convertDirectoryStreamToStream[E](directoryStream: DirectoryStream[E]): Stream[E] =
    directoryStream.iterator.toStream
}

object GluinoPath extends GluinoPath