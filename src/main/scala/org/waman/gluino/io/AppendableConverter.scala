package org.waman.gluino.io

import java.nio.charset.Charset
import java.nio.file.{Files, Path}

import org.waman.gluino.nio.PathWrapper

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

  private val bufferSize = 1024

  implicit def convertInputStreamToOutputtable(input: InputStream): Outputtable = new Outputtable{
    override def outputTo(output: OutputStream): Unit = {
      val bis = input match{
        case _: BufferedInputStream => input
        case _: InputStream => new BufferedInputStream(input)
      }

      bis.withInputStream{ is =>
        @tailrec
        def copyBytes(is: InputStream, bytes: Array[Byte]): Unit = {
          val n = is.read(bytes)
          n match {
            case -1 =>
            case _ if n >= 0 =>
              output.write(bytes, 0, n)
              copyBytes(is, bytes)
          }
        }

        copyBytes(is, new Array[Byte](bufferSize))
      }
    }
  }

  implicit def convertFileToOutputtable(file: File): Outputtable =
    convertInputStreamToOutputtable(new FileInputStream(file))

  implicit def convertPathToOutputtable(path: Path): Outputtable =
    convertInputStreamToOutputtable(Files.newInputStream(path))

  implicit def convertFileWrapperToOutputtable(wrapper: FileWrapper): Outputtable =
    convertInputStreamToOutputtable(wrapper.newInputStream())

  implicit def convertPathWrapperToOutputtable(wrapper: PathWrapper): Outputtable =
    convertInputStreamToOutputtable(wrapper.newInputStream())

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

  implicit def convertFileToWritable(fileWithCharset: (File, Charset)): Writable =
    convertBufferedReaderToWritable(new FileWrapper(fileWithCharset._1).newReader(fileWithCharset._2))

  implicit def convertPathToWritable(path: Path): Writable =
    convertBufferedReaderToWritable(Files.newBufferedReader(path))

  implicit def convertPathToWritable(pathWithCharset: (Path, Charset)): Writable =
    convertBufferedReaderToWritable(new PathWrapper(pathWithCharset._1).newReader(pathWithCharset._2))

  implicit def convertFileWrapperToWritable(wrapper: FileWrapper): Writable =
    convertBufferedReaderToWritable(wrapper.newReader(GluinoIO.defaultCharset))

  implicit def convertFileWrapperToWritable(wrapperWithCharset: (FileWrapper, Charset)): Writable =
    convertBufferedReaderToWritable(wrapperWithCharset._1.newReader(wrapperWithCharset._2))

  implicit def convertPathWrapperToWritable(wrapper: PathWrapper): Writable =
    convertBufferedReaderToWritable(wrapper.newReader(GluinoIO.defaultCharset))

  implicit def convertPathWrapperToWritable(wrapperWithCharset: (PathWrapper, Charset)): Writable =
    convertBufferedReaderToWritable(wrapperWithCharset._1.newReader(wrapperWithCharset._2))
}
