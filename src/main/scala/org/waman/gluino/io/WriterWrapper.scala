package org.waman.gluino.io

import java.io.{BufferedWriter, Writer}

class WriterWrapper private (private[io] val writer: BufferedWriter){

  def withWriter(consumer: BufferedWriter => Unit): Unit =
    try {
      consumer(writer)
    }finally{
      writer.flush()
      writer.close()
    }

  def writeLine(line: String): Unit = {
    writer.write(line)
    writer.write(GluinoIO.lineSep)
  }

  def writeLines(lines: Seq[String]): Unit = lines.foreach(writer.write)

  def append(input: Writable): Unit = input.writeTo(writer)
  def <<(input: Writable): WriterWrapper = { append(input); this }
}

object WriterWrapper{

  def apply(writer: Writer): WriterWrapper = writer match {
    case bw: BufferedWriter => new WriterWrapper(bw)
    case _ => new WriterWrapper(new BufferedWriter(writer))
  }
}