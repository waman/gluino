package org.waman.gluino.number

import scala.language.implicitConversions

trait GluinoNumber {

  implicit def wrapInteger(i: Int): IntWrapper = new IntWrapper(i)
}

object GluinoNumber extends GluinoNumber
