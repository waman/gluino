package org.waman.gluino

import java.nio.file.FileSystems

import org.scalatest.Tag

object WindowsSpecific extends Tag("org.waman.gluino.tags.WindowsSpecific")

trait WindowsAssumption{
  assume(System.getProperty("os.name").toLowerCase contains "windows")
  assume(FileSystems.getDefault.supportedFileAttributeViews() contains "acl")
}