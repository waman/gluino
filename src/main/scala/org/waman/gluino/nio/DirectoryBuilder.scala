package org.waman.gluino.nio

import java.nio.file.{Files, Path}
import scala.collection.JavaConversions._

abstract class DirectoryBuilder{

  val baseDir: Path
  private var currentDir: Path = null

  private def initCurrentDir(): Unit =
    if(this.currentDir == null)
      this.currentDir = this.baseDir

  def dir(name: String)(buildDir: => Unit): Path = {
    initCurrentDir()
    val newDir = Files.createDirectory(this.currentDir.resolve(name))

    val oldDir = this.currentDir
    this.currentDir = newDir
    buildDir
    this.currentDir = oldDir

    newDir
  }

  def file(name: String): Path = {
    initCurrentDir()
    Files.createFile(this.currentDir.resolve(name))
  }

  def file(name: String, content: String): Path = {
    val newFile = file(name)
    Files.write(newFile, Seq(content))
    newFile
  }
}
