package org.waman.gluino

import java.nio.file.Files

import org.scalatest.{FreeSpec, Matchers}

class GluinoCustomSpec extends FreeSpec with Matchers with FourPhaseInformer{

  trait TempFileFixture{
    val path = Files.createTempFile(null, null)
  }
}