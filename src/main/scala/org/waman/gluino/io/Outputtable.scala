package org.waman.gluino.io

import java.io._

trait Outputtable {
  def outputTo(output: OutputStream): Unit
}