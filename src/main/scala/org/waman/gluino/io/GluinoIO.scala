package org.waman.gluino.io

import java.nio.charset.{StandardCharsets, Charset}

import scala.language.implicitConversions
import java.io._

trait GluinoIO{

  val lineSeparator: String = System.getProperty("line.separator")
  val tmpdir: String = System.getProperty("java.io.tmpdir")
  val defaultCharset: Charset = StandardCharsets.UTF_8

  implicit def convertStringToCharset(charset: String): Charset = charset match {
    case "default" => Charset.defaultCharset
    case _ => Charset.forName(charset)
  }

  implicit def wrapCloseable(closeable: Closeable): CloseableWrapper = new CloseableWrapper(closeable)

  implicit def wrapInputStream(input: InputStream): InputStreamWrapper = new InputStreamWrapper(input)
  implicit def wrapOutputStream(output: OutputStream): OutputStreamWrapper = new OutputStreamWrapper(output)

  implicit def wrapReader(reader: Reader): ReaderWrapper = ReaderWrapper(reader)
  implicit def wrapWriter(writer: Writer): WriterWrapper = WriterWrapper(writer)
  implicit def wrapPrintWriter(writer: PrintWriter): PrintWriterWrapper = new PrintWriterWrapper(writer)

  implicit def wrapObjectInputStream(ois: ObjectInputStream): ObjectInputStreamWrapper =
    new ObjectInputStreamWrapper(ois)
  implicit def wrapObjectOutputStream(oos: ObjectOutputStream): ObjectOutputStreamWrapper =
    new ObjectOutputStreamWrapper(oos)
}

object GluinoIO extends GluinoIO