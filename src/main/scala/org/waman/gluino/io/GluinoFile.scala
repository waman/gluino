package org.waman.gluino.io

import java.io.File

import scala.language.implicitConversions

trait GluinoFile extends AppendableConverter{

  val tempDir: File = new File(tmpdir)

  def createTempFile(dir: File = tempDir, prefix: String = "gluino-", suffix: String = null,
                    deleteOnExit: Boolean = false): File = {
    val file = File.createTempFile(prefix, suffix, dir)
    if(deleteOnExit)file.deleteOnExit()
    file
  }

  def createTempDirectory(dir: File = tempDir, prefix: String = "gluino-",
                          deleteOnExit: Boolean = false): File = {
    val td = File.createTempFile(prefix, "", dir)
    td.delete()
    td.mkdir()
    if(deleteOnExit)td.deleteOnExit()
    td
  }

  implicit def wrapFile(file: File): FileWrapper = new FileWrapper(file)

  //***** FileType *****
  implicit object FileFileTypeFilterProvider extends FileTypeFilterProvider[File] {

    override def getFilterForFile: File => Boolean =
      file => new FileWrapper(file).isFile

    override def getFilterForDirectory: File => Boolean =
      file => new FileWrapper(file).isDirectory
  }
}

object GluinoFile extends GluinoFile