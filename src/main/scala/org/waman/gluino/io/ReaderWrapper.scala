package org.waman.gluino.io

import java.io.{BufferedReader, Reader, Writer}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.matching.Regex

class ReaderWrapper private (private[io] val reader: BufferedReader){

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
      if(filter(line))writer.write(line)
    }
  }

  def filterLine(writer: Writer)(filter: String => Boolean): Unit =
    filterLine(filter).writeTo(writer)

  def readLines(): Seq[String] = {
    val lines = new mutable.MutableList[String]
    eachLine(lines += _)
    lines.toSeq
  }
}

object ReaderWrapper{

  def apply(reader: Reader): ReaderWrapper = reader match {
    case br: BufferedReader => new ReaderWrapper(br)
    case _ => new ReaderWrapper(new BufferedReader(reader))
  }
}