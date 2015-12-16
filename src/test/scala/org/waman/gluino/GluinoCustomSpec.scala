package org.waman.gluino

import java.nio.file.{FileSystemException, FileSystems, Files}

import org.scalatest.{FreeSpec, Matchers}
import org.waman.gluino.nio.GluinoPath

trait GluinoCustomSpec extends FreeSpec with Matchers with FourPhaseInformer{

  //***** Utility methods *****
  def convertImplicitly[T](t: T) = t

  //***** OS *****
  trait WindowsRequirement{
    assume(System.getProperty("os.name").toLowerCase contains "windows")
  }

  trait WindowsAclRequirement extends WindowsRequirement{
    assume(FileSystems.getDefault.supportedFileAttributeViews() contains "acl")
  }

  trait WindowsAdministratorRequirement extends WindowsRequirement{
    assumeWindowsAdministrated()
  }

  private def assumeWindowsAdministrated(): Unit = {
    val symlink = GluinoPath.createTempFile()
    Files.delete(symlink)
    val target = GluinoPath.createTempFile(deleteOnExit = true)
    try{
      Files.createSymbolicLink(symlink, target)
    }catch{
      case ex: FileSystemException => cancel(ex.getMessage)
    }
  }

  trait PosixFileSystemRequirement{
    assume(FileSystems.getDefault.supportedFileAttributeViews() contains "posix")
  }
}
