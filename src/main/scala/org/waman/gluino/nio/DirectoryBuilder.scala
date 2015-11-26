package org.waman.gluino.nio

import java.nio.file.{Files, Path}
import scala.collection.JavaConversions._

abstract class DirectoryBuilder{

  val baseDir: Path
  private var currentDir: Path = null

  private def initCurrentDir(): Unit =
    if(this.currentDir == null)
      this.currentDir = this.baseDir

  protected def emptyDir(name: String): Path = {
    initCurrentDir()
    Files.createDirectory(this.currentDir.resolve(name))
  }

  protected def dir(name: String)(buildDir: => Unit): Path = {
    val newDir = emptyDir(name)

    val oldDir = this.currentDir
    this.currentDir = newDir
    buildDir
    this.currentDir = oldDir

    newDir
  }

  protected def file(name: String): Path = {
    initCurrentDir()
    Files.createFile(this.currentDir.resolve(name))
  }

  protected def file(name: String, content: String): Path =
    Files.write(file(name), Seq(content))
}
