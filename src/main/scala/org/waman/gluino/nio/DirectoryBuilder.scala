package org.waman.gluino.nio

import java.nio.file.attribute.FileAttribute
import java.nio.file.{Files, Path}

abstract class DirectoryBuilder{

  val baseDir: Path
  private var currentDir: Path = null

  private def initCurrentDirIfNull(): Unit =
    if(this.currentDir == null)
      this.currentDir = this.baseDir

  protected def emptyDir(name: String, attrs: FileAttribute[_]*): Path = {
    initCurrentDirIfNull()
    Files.createDirectory(this.currentDir.resolve(name), attrs:_*)
  }

  protected def dir(name: String, attrs: FileAttribute[_]*)(buildDir: => Unit): Path = {
    val newDir = emptyDir(name, attrs:_*)

    val oldDir = this.currentDir
    this.currentDir = newDir
    buildDir
    this.currentDir = oldDir

    newDir
  }

  protected def file(name: String, attrs: FileAttribute[_]*): Path = {
    initCurrentDirIfNull()
    Files.createFile(this.currentDir.resolve(name), attrs:_*)
  }
  
  protected def link(name: String, target: Path): Path = {
    initCurrentDirIfNull()
    Files.createLink(this.currentDir.resolve(name), target)
  }
  
  protected def symbolicLink(name: String, target: Path, attrs: FileAttribute[_]*): Path = {
    initCurrentDirIfNull()
    Files.createSymbolicLink(this.currentDir.resolve(name), target, attrs:_*)
  }
}
