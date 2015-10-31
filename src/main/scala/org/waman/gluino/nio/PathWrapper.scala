package org.waman.gluino.nio

import java.nio.file.{Paths, Path}

class PathWrapper(path: Path){

  def /(child: String): Path = /(Paths.get(child))
  def /(child: Path)  : Path = path.resolve(child)
  def \(child: String): Path = /(child)
  def \(child: Path)  : Path = /(child)
}