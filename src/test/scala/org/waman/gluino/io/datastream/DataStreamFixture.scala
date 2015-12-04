package org.waman.gluino.io.datastream

import java.io.{DataInputStream, DataOutputStream}
import java.nio.file.{Files, Path}

import org.waman.gluino.nio.GluinoPath

trait DataStreamFixture {

  lazy val readOnlyDataPath = createReadOnlyDataPath()
  lazy val readOnlyDataFile = readOnlyDataPath.toFile

  private def createReadOnlyDataPath(): Path = {
    val path = GluinoPath.createTempFile(deleteOnExit = true)
    val oos = new DataOutputStream(Files.newOutputStream(path))
    try{
      oos.writeInt(1)
      oos.writeLong(2L)
      oos.writeDouble(3.0d)
      oos.writeUTF("UTF")
      oos.writeBytes("string")

      oos.flush()
    }finally oos.close()

    path
  }

  trait DataInputStreamFixture{
    val input = Files.newInputStream(readOnlyDataPath)
    val dataInput = new DataInputStream(input)
  }

  trait DataFileFixture{
    val dataPath = createReadOnlyDataPath()
    lazy val dataFile = dataPath.toFile
  }

}
