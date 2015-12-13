package org.waman.gluino

import java.nio.file.FileSystems

import org.scalatest.{Matchers, FreeSpec}

trait GluinoCustomSpec extends FreeSpec with Matchers with FourPhaseInformer{

  //***** Utility methods *****
  def convertImplicitly[T](t: T) = t

  //***** OS *****
  trait WindowsAssumption{
    assume(System.getProperty("os.name").toLowerCase contains "windows")
    assume(FileSystems.getDefault.supportedFileAttributeViews() contains "acl")
  }

  trait PosixFileSystemAssumption{
    assume(FileSystems.getDefault.supportedFileAttributeViews() contains "posix")
  }
}
