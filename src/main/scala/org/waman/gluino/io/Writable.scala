package org.waman.gluino.io

import java.io.Writer

trait Writable{
  def writeTo(writer: Writer): Unit
}