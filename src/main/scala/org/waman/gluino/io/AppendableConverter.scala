package org.waman.gluino.io

import java.nio.file.{Files, Path}

import scala.annotation.tailrec
import scala.language.implicitConversions
import java.io._

trait AppendableConverter extends GluinoIO{

  //***** Outputtable *****
  implicit def convertByteArrayToOutputtable(bytes: Array[Byte]): Outputtable = new Outputtable {
    override def outputTo(output: OutputStream): Unit = output.write(bytes)
  }

  implicit def convertByteSeqToOutputtable(byteSeq: Seq[Byte]): Outputtable = new Outputtable {
    override def outputTo(output: OutputStream): Unit = output.write(byteSeq.toArray)
  }

  implicit def convertInputStreamToOutputtable(input: InputStream): Outputtable = new Outputtable{
    override def outputTo(output: OutputStream): Unit = {
      val bis = input match{
        case _: BufferedInputStream => input
        case _: InputStream => new BufferedInputStream(input)
      }

      @tailrec
      def copyBytes(n: Int): Unit = n match {
        case 0 =>
        case _ if n > 0 =>
          val bytes = new Array[Byte](n)
          bis.read(bytes)
          output.write(bytes)
          copyBytes(bis.available())
      }

      copyBytes(bis.available())
      bis.close()
    }
  }

  implicit def convertFileToOutputtable(file: File): Outputtable =
    convertInputStreamToOutputtable(new FileInputStream(file))

  implicit def convertPathToOutputtable(path: Path): Outputtable =
    convertInputStreamToOutputtable(Files.newInputStream(path))

  //***** Writable *****
  implicit def convertStringToWritable(text: String): Writable = new Writable{
    override def writeTo(writer: Writer): Unit = writer.write(text)
  }

  implicit def convertReaderToWritable(reader: Reader): Writable = reader match {
    case br: BufferedReader => convertBufferedReaderToWritable(br)
    case _ : Reader         => convertBufferedReaderToWritable(new BufferedReader(reader))
  }

  implicit def convertBufferedReaderToWritable(reader: BufferedReader): Writable = new Writable{
    override def writeTo(writer: Writer): Unit = reader.eachLine(line => writer.writeLine(line))
                                              // make arg explicit due to overloading eachLine()
  }

  implicit def convertFileToWritable(file: File): Writable =
    convertBufferedReaderToWritable(new BufferedReader(new FileReader(file)))

  implicit def convertPathToWritable(path: Path): Writable =
    convertBufferedReaderToWritable(Files.newBufferedReader(path))
}
