package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

import scala.collection.mutable

class FileWrapper(file: File) extends ScalaJdkIO[FileWrapper]{

  // Byte array, Byte stream
  override def bytes: Array[Byte] = {
    var bytes = mutable.ArrayBuffer[Byte]()
    newInputStream().eachByte(bytes += _)
    bytes.toArray
  }

  override def bytes_=(bytes: Array[Byte]): Unit =
    newOutputStream().withOutputStream(_.write(bytes))

  // new Input/Output stream
  override def newInputStream(): InputStream =
    new BufferedInputStream(new FileInputStream(file))

  override def newOutputStream(append: Boolean): OutputStream =
    new BufferedOutputStream(new FileOutputStream(file))

  // new Reader/Writer/PrintWriter
  override def newReader(charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))

  override def newWriter(charset: Charset, append: Boolean): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset))
}
