package org.waman.gluino.io

import scala.language.implicitConversions
import java.io.{BufferedReader, InputStream, OutputStream, Writer}

trait AppendableConverter extends GluinoIO{

  implicit def convertByteArrayToOutputtable(bytes: Array[Byte]): Outputtable = new Outputtable {
    override def outputTo(output: OutputStream): Unit = output.write(bytes)
  }

  implicit def convertByteSeqToOutputtable(byteSeq: Seq[Byte]): Outputtable = new Outputtable {
    override def outputTo(output: OutputStream): Unit = output.write(byteSeq.toArray)
  }

  implicit def convertInputStreamToOutputtable(input: InputStream): Outputtable = new Outputtable{
    override def outputTo(output: OutputStream): Unit =
      input.eachByte(output write _)
  }

  implicit def convertStringToWritable(text: String): Writable = new Writable{
    override def writeTo(writer: Writer): Unit = writer.write(text)
  }

  implicit def convertBufferedReaderToWritable(reader: BufferedReader): Writable = new Writable{
    override def writeTo(writer: Writer): Unit = reader.eachLine(line => writer.writeLine(line))
                                              // make arg explicit for eachLine() overloading
  }
}
