package org.waman.gluino.io

import java.io._
import java.nio.charset.Charset

trait OutputStreamWrapperLike[T <: OutputStreamWrapperLike[T]]
    extends GluinoIO with WriterWrapperLike[T]{ self: T =>

  protected def getOutputStream: OutputStream

  def withOutputStream(consumer: OutputStream => Unit): Unit = {
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

  protected def newWriter(charset: Charset): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(getOutputStream, charset))

  def withWriter(charset: Charset)(consumer: BufferedWriter => Unit): Unit =
    newWriter(charset).withWriter(consumer)

  //***** PrintWriterWrapper methods with Charset  *****
  override protected def getPrintWriter: PrintWriter = newPrintWriter(defaultCharset)

  protected def newPrintWriter(charset: Charset): PrintWriter =
    new PrintWriter(newWriter(charset))

  def withPrintWriter(charset: Charset)(consumer: PrintWriter => Unit): Unit =
    newPrintWriter(charset).withPrintWriter(consumer)

  //***** ObjectOutputStream, DataOutputStream *****
  def newObjectOutputStream: ObjectOutputStream = new ObjectOutputStream(getOutputStream)

  def withObjectOutputStream(consumer: ObjectOutputStream => Unit): Unit = {
    val os = getOutputStream
    val oos = os match{
      case ObjectOutputStream => os.asInstanceOf[ObjectOutputStream]
      case _ => new ObjectOutputStream(os)
    }
    try{
      consumer(oos)
    }finally{
      oos.close()
    }
  }

  def newDataOutputStream: DataOutputStream = new DataOutputStream(getOutputStream)

  def withDataOutputStream(consumer: DataOutputStream => Unit): Unit = {
    val os = getOutputStream
    val dos = os match{
      case DataOutputStream => os.asInstanceOf[DataOutputStream]
      case _ => new DataOutputStream(os)
    }
    try{
      consumer(dos)
    }finally{
      dos.close()
    }
  }
}
