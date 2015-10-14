package org.waman.gluino.nio

import java.io._

trait Outputtable {
  def outputTo(output: OutputStream): Unit
}