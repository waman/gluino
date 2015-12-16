package org.waman.gluino.io

import java.nio.charset.{StandardCharsets, Charset}

import org.waman.gluino.io.datastream.{DataOutputStreamWrapper, DataInputStreamWrapper}
import org.waman.gluino.io.objectstream.{ObjectInputStreamWrapper, ObjectOutputStreamWrapper}

import scala.language.implicitConversions
import java.io._

trait GluinoIO{

  implicit def convertStringToCharset(charset: String): Charset = charset match {
    case "default" => Charset.defaultCharset
    case _ => Charset.forName(charset)
  }

  implicit def wrapCloseable(closeable: Closeable): CloseableWrapper = new CloseableWrapper(closeable)

  // InputStream/OutputStream
  implicit def wrapInputStream(input: InputStream): InputStreamWrapper = InputStreamWrapper(input)
  implicit def wrapOutputStream(output: OutputStream): OutputStreamWrapper = OutputStreamWrapper(output)

  // Reader/Writer
  implicit def wrapReader(reader: Reader): ReaderWrapper = ReaderWrapper(reader)
  implicit def wrapWriter(writer: Writer): WriterWrapper = WriterWrapper(writer)
  implicit def wrapPrintWriter(writer: PrintWriter): PrintWriterWrapper = PrintWriterWrapper(writer)

  // DataStream, ObjectStream
  implicit def wrapDataInputStream(dis: DataInputStream): DataInputStreamWrapper =
    DataInputStreamWrapper(dis)
  implicit def wrapDataOutputStream(dos: DataOutputStream): DataOutputStreamWrapper =
    DataOutputStreamWrapper(dos)

  implicit def wrapObjectInputStream(ois: ObjectInputStream): ObjectInputStreamWrapper =
    ObjectInputStreamWrapper(ois)
  implicit def wrapObjectOutputStream(oos: ObjectOutputStream): ObjectOutputStreamWrapper =
    ObjectOutputStreamWrapper(oos)
}

object GluinoIO extends GluinoIO{

  val lineSeparator: String = System.getProperty("line.separator")
  val tmpdir: String = System.getProperty("java.io.tmpdir")
  val defaultCharset: Charset = StandardCharsets.UTF_8
}