package org.waman.gluino.io

import java.io.File

trait GluinoFile extends AppendableConverter{

  implicit def convertFileToScalaJdkFile(file: File): ScalaJdkFile = new ScalaJdkFile(file)
}

object GluinoFile extends GluinoFile