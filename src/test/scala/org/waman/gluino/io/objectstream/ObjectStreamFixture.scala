package org.waman.gluino.io.objectstream

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Files, Path}

import org.waman.gluino.nio.GluinoPath

trait ObjectStreamFixture {

  val objectContent = List("1", new Integer(2), BigDecimal(3))

  lazy val readOnlyObjectPath = createReadOnlyObjectPath()
  lazy val readOnlyObjectFile = readOnlyObjectPath.toFile

  private def createReadOnlyObjectPath(): Path = {
    val path = GluinoPath.createTempFile(deleteOnExit = true)
    val oos = new ObjectOutputStream(Files.newOutputStream(path))
    try{
      objectContent.foreach(oos.writeObject(_))
      oos.flush()
    }finally oos.close()

    path
  }

  trait ObjectInputStreamFixture{
    val input = Files.newInputStream(readOnlyObjectPath)
    val objectInput = new ObjectInputStream(input)
  }

  trait ObjectFileFixture{
    val objectPath = createReadOnlyObjectPath()
    lazy val objectFile = objectPath.toFile
  }
}
