package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset
import java.nio.file.{StandardOpenOption, Files, Path}

import org.waman.gluino.io.datastream.DataOutputStreamWrapperLike
import org.waman.gluino.io.objectstream.ObjectOutputStreamWrapperLike

trait OutputStreamWrapperLike[T <: OutputStreamWrapperLike[T]]
  extends GluinoIO
  with DataOutputStreamWrapperLike
  with ObjectOutputStreamWrapperLike
  with WriterWrapperLike[T]{ self: T =>

  protected def getOutputStream: OutputStream

  def withOutputStream[R](consumer: OutputStream => R): R = {
    val os = getOutputStream
    try{
      consumer(os)
    }finally{
      os.flush()
      os.close()
    }
  }

  // append, <<
  def append(input: Outputtable): Unit = input.outputTo(getOutputStream)
  def <<(input: Outputtable): T = { append(input); this }

  //***** WriterWrapper methods with Charset  *****
  override protected def getWriter: BufferedWriter = newWriter(defaultCharset)

  def newWriter(charset: Charset): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(getOutputStream, charset))

  def withWriter[R](charset: Charset)(consumer: BufferedWriter => R): R =
    newWriter(charset).withWriter(consumer)

  //***** PrintWriterWrapper methods with Charset  *****
  override def newPrintWriter(): PrintWriter = newPrintWriter(defaultCharset)

  def newPrintWriter(charset: Charset): PrintWriter =
    new PrintWriter(newWriter(charset))

  def withPrintWriter[R](charset: Charset)(consumer: PrintWriter => R): R =
    newPrintWriter(charset).withPrintWriter(consumer)

  //***** ObjectOutputStream, DataOutputStream *****
  override protected def getObjectOutputStream: ObjectOutputStream = newObjectOutputStream()
  override protected def getDataOutputStream: DataOutputStream = newDataOutputStream()

  def newObjectOutputStream(): ObjectOutputStream = new ObjectOutputStream(getOutputStream)
  def newDataOutputStream(): DataOutputStream = new DataOutputStream(getOutputStream)
}

class OutputStreamWrapper private (private[io] val stream: OutputStream)
    extends OutputStreamWrapperLike[OutputStreamWrapper] with Closeable{

  override protected def getOutputStream: OutputStream = stream

  override def close(): Unit = {
    stream.flush()
    stream.close()
  }
}

object OutputStreamWrapper{

  def apply(stream: OutputStream): OutputStreamWrapper = new OutputStreamWrapper(stream)

  def apply(path: Path, append: Boolean): OutputStreamWrapper =
    if(append) apply(Files.newOutputStream(path, StandardOpenOption.APPEND))
    else apply(Files.newOutputStream(path))

  def apply(file: File, append: Boolean): OutputStreamWrapper = apply(file.toPath, append)
}