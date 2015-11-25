package org.waman.gluino.nio

import java.nio.file.Path

abstract class DirectoryBuilder extends GluinoPath{

  val baseDir: Path
  private var currentDir: Path = null

  private def initCurrentDir(): Unit =
    if(this.currentDir == null)
      this.currentDir = this.baseDir

  def dir(name: String)(buildDir: => Unit): Path = {
    initCurrentDir()
    val newDir = (this.currentDir / name).createDirectory()

    val oldDir = this.currentDir
    this.currentDir = newDir
    buildDir
    this.currentDir = oldDir

    newDir
  }

  def file(name: String): Path = {
    initCurrentDir()
    (this.currentDir / name).createFile()
  }

  def file(name: String, content: String): Path = {
    val newFile = file(name)
    newFile.write(Seq(content))
    newFile
  }
}
