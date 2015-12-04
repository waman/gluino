package org.waman.gluino

import org.scalatest.{Matchers, FreeSpec}

trait GluinoCustomSpec extends FreeSpec with Matchers with FourPhaseInformer{

  //***** Utility methods *****
  def convertImplicitly[T](t: T) = t

}
