package org.waman.gluino.nio

import java.io.Writer

trait Writable{
  def writeTo(writer: Writer): Unit
}