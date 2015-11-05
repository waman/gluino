package org.waman.gluino.io

import java.nio.charset.Charset

import scala.language.implicitConversions
import java.io._

trait GluinoIO{

  val lineSeparator: String = System.getProperty("line.separator")
  val tmpdir: String = System.getProperty("java.io.tmpdir")

  implicit def convertStringToCharset(charset: String): Charset = charset match {
    case "default" => Charset.defaultCharset
    case _ => Charset.forName(charset)
  }

  implicit def wrapInputStream(input: InputStream): InputStreamWrapper = new InputStreamWrapper(input)
  implicit def wrapOutputStream(output: OutputStream): OutputStreamWrapper = new OutputStreamWrapper(output)

  implicit def wrapReader(reader: Reader): ReaderWrapper = ReaderWrapper(reader)
  implicit def wrapWriter(writer: Writer): WriterWrapper = WriterWrapper(writer)
  implicit def wrapPrintWriter(writer: PrintWriter): PrintWriterWrapper = new PrintWriterWrapper(writer)
}

object GluinoIO extends GluinoIO