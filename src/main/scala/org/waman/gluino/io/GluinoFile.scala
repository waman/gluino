package org.waman.gluino.io

import scala.language.implicitConversions
import java.io.File

trait GluinoFile extends AppendableConverter{

  val tempDir: File = new File(tmpdir)

//  def createTempFile
//  (dir: Path = tmpdir, prefix: String = null, suffix: String = null): File =
//    File.createTempFile(dir, prefix, suffix, attributes.toArray:_*)
//
//  def createTempDirectory
//  (dir: Path = tmpdir, prefix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
//    Files.createTempDirectory(dir, prefix, attributes.toArray:_*)

  implicit def wrapFile(file: File): FileWrapper = new FileWrapper(file)
}

object GluinoFile extends GluinoFile