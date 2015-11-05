package org.waman.gluino.io

import java.io.{BufferedReader, Writer}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.matching.Regex

trait ReaderWrapperLike {

  protected def reader: BufferedReader

  //***** withReader *****
  def withReader(consumer: BufferedReader => Unit): Unit = try{
    consumer(reader)
  }finally{
    reader.close()
  }

  //***** text (String) *****
  def eachChar(consumer: Char => Unit): Unit = withReader{ reader =>
    @tailrec
    def consumeChar(ch: Int): Unit = ch match {
      case -1 =>
      case _  =>
        consumer(ch.asInstanceOf[Char])
        consumeChar(reader.read())
    }

    consumeChar(reader.read())
  }

  def transformChar(writer: Writer)(map: Char => Char): Unit =
    WriterWrapper(writer).withWriter{ writer =>
      eachChar(c => writer.write(map(c)))
    }

  def text: String = {
    var sb = new mutable.StringBuilder
    eachChar(sb += _)
    sb.toString()
  }

  //***** lines *****
  def eachLine(consumer: String => Unit): Unit = withReader { reader =>
    @tailrec
    def consumeLine(line: String): Unit = line match {
      case null =>
      case _    =>
        consumer(line)
        consumeLine(reader.readLine())
    }

    consumeLine(reader.readLine())
  }

  def eachLine(n0: Int = 0)(consumer: (String, Int) => Unit) = withReader{ reader =>
    @tailrec
    def consumeLineWithIndex(line: String, n: Int): Unit = line match {
      case null =>
      case _    =>
        consumer(line, n)
        consumeLineWithIndex(reader.readLine(), n+1)
    }

    consumeLineWithIndex(reader.readLine(), n0)
  }

  def splitEachLine(regex: Regex)(consumer: List[String] => Unit): Unit =
    eachLine(s => consumer(regex.split(s).toList))

  def filterLine(filter: String => Boolean): Writable = new Writable{
    override def writeTo(writer: Writer): Unit = eachLine{ line =>
      if(filter(line))writer write line
    }
  }

  def filterLine(writer: Writer)(filter: String => Boolean): Unit =
    WriterWrapper(writer).withWriter{ writer =>
      filterLine(filter).writeTo(writer)
    }

  def transformLine(writer: Writer)(map: String => String): Unit =
    WriterWrapper(writer).withWriter{ writer =>
      eachLine{ line =>
        writer.write(map(line))
        writer.write(GluinoIO.lineSeparator)
      }
    }

  def readLines(): Seq[String] = {
    val lines = new mutable.MutableList[String]
    eachLine(lines += _)
    lines.toSeq
  }
}
