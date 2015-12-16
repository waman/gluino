package org.waman.gluino.nio

import java.net.URI
import java.nio.file._
import java.nio.file.attribute._

import org.waman.gluino.io.{GluinoIO, AppendableConverter, FileTypeFilterProvider}

import scala.collection.JavaConversions._
import scala.language.implicitConversions

trait GluinoPath extends AttributeConverter with AppendableConverter{

  //***** Path Creation *****
  def path(s: String): Path = Paths.get(s)
  def path(seq: String*): Path = Paths.get(seq.head, seq.tail:_*)
  def path(uri: URI): Path = Paths.get(uri)
  implicit def convertStringToPath(s: String): Path = path(s)
  implicit def convertSeqToPath(seq: Seq[String]): Path = path(seq:_*)
  implicit def convertUriToPath(uri: URI): Path = path(uri)


  //***** Path Wrappers *****
  implicit def wrapPath(path: Path): PathWrapper = new PathWrapper(path)
  implicit def convertPathToFilesCategory(path: Path): FilesCategory = new FilesCategory(path)

  //***** FileType *****
  implicit object PathFileTypeFilterProvider extends FileTypeFilterProvider[Path] {

    override def getFilterForFile: Path => Boolean =
      path => new PathWrapper(path).isFile

    override def getFilterForDirectory: Path => Boolean =
      path => new PathWrapper(path).isDirectory
  }

  //***** Conversion of Path to Stream, Reader/Writer *****
//  implicit def convertPathToFile(path: Path): java.io.File = path.toFile
//  implicit def convertPathToInputStream(path: Path): InputStream = Files.newInputStream(path)
//  implicit def convertPathToOutputStream(path: Path): OutputStream = Files.newOutputStream(path)
//  implicit def convertPathToBufferedReader(path: Path): BufferedReader = Files.newBufferedReader(path)
//  implicit def convertPathToBufferedWriter(path: Path): BufferedWriter = Files.newBufferedWriter(path)

  //***** Conversion of DirectoryStream to Stream *****
  implicit def convertDirectoryStreamToStream[E](directoryStream: DirectoryStream[E]): Stream[E] =
    directoryStream.iterator.toStream
}

object GluinoPath extends GluinoPath{

  //***** Temporal File/Directory *****
  val tempDir: Path = Paths.get(GluinoIO.tmpdir)

  def createTempFile(dir: Path = tempDir,
                     prefix: String = null,
                     suffix: String = null,
                     deleteOnExit: Boolean = false,
                     attrs: Seq[FileAttribute[_]] = Seq()): Path = {
    val file = Files.createTempFile(dir, prefix, suffix, attrs.toArray:_*)
    if(deleteOnExit)file.toFile.deleteOnExit()
    file
  }

  def createTempDirectory(dir: Path = tempDir,
                          prefix: String = null,
                          deleteOnExit: Boolean = false,
                          attrs: Seq[FileAttribute[_]] = Seq()): Path = {
    val td = Files.createTempDirectory(dir, prefix, attrs.toArray:_*)
    if(deleteOnExit)td.toFile.deleteOnExit()
    td
  }
}