package org.waman.gluino.io

import java.io.File

trait GluinoFile extends AppendableConverter{

  val tmpdirFile: File = new File(GluinoIO.tmpdir)

//  def createTempFile
//  (dir: Path = tmpdir, prefix: String = null, suffix: String = null): File =
//    File.createTempFile(dir, prefix, suffix, attributes.toArray:_*)
//
//  def createTempDirectory
//  (dir: Path = tmpdir, prefix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
//    Files.createTempDirectory(dir, prefix, attributes.toArray:_*)

  implicit def convertFileToScalaJdkFile(file: File): ScalaJdkFile = new ScalaJdkFile(file)
}

object GluinoFile extends GluinoFile