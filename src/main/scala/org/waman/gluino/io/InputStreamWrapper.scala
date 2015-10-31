package org.waman.gluino.io

import java.io.InputStream

import scala.annotation.tailrec

class InputStreamWrapper(private[io] val inputStream: InputStream){

  def eachByte(consumer: Byte => Unit): Unit = withInputStream{ input =>
    @tailrec
    def consumeByte(byte: Int): Unit = byte match {
      case -1 =>
      case _ =>
        consumer(byte.asInstanceOf[Byte])
        consumeByte(input.read())
    }

    consumeByte(input.read())
  }

  def withInputStream(consumer: InputStream => Unit): Unit = try{
    consumer(inputStream)
  }finally{
    inputStream.close()
  }
}
