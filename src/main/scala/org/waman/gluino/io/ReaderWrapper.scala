package org.waman.gluino.io

import java.io._
import java.nio.file.{Files, Path}

import org.waman.gluino.io.GluinoIO.lineSeparator

import scala.annotation.tailrec
import scala.util.matching.Regex

trait ReaderWrapperLike extends GluinoIO{

  protected def getReader: BufferedReader

  //***** withReader *****
  def withReader[R](consumer: BufferedReader => R): R = {
    val reader = getReader

    try consumer(reader)
    finally reader.close()
  }

  //***** text (String) *****
  def eachChar(consumer: Char => Unit): Unit = withReader{ reader =>
    @tailrec
    def consumeChar(r: BufferedReader): Unit = r.read() match {
      case -1 =>
      case ch =>
        consumer(ch.asInstanceOf[Char])
        consumeChar(r)
    }

    consumeChar(reader)
  }

  def transformChar(writer: Writer)(map: Char => Char): Unit =
    writer.withWriter{ w =>
      eachChar(c => w.write(map(c)))
    }

  def text: String = readLines.map(_ + lineSeparator).foldLeft("")(_ + _)

  //***** lines *****
  def eachLine(consumer: String => Unit): Unit = withReader { reader =>
    @tailrec
    def consumeLine(r: BufferedReader): Unit = r.readLine() match {
      case null =>
      case line =>
        consumer(line)
        consumeLine(reader)
    }

    consumeLine(reader)
  }

  def eachLine(n0: Int = 0)(consumer: (String, Int) => Unit) = withReader{ reader =>
    @tailrec
    def consumeLineWithIndex(r: BufferedReader, n: Int): Unit = r.readLine() match {
        case null =>
        case line =>
          consumer(line, n)
          consumeLineWithIndex(r, n+1)
      }

    consumeLineWithIndex(reader, n0)
  }

  def splitEachLine(regex: Regex)(consumer: List[String] => Unit): Unit =
    eachLine(s => consumer(regex.split(s).toList))

  def filterLine(filter: String => Boolean): Writable = new Writable{
    override def writeTo(writer: Writer): Unit = eachLine{ line =>
      if(filter(line))writer.write(line)
    }
  }

  def filterLine(writer: Writer)(filter: String => Boolean): Unit =
    writer.withWriter{ w =>
      filterLine(filter).writeTo(w)
    }

  def transformLine(writer: Writer)(map: String => String): Unit =
    writer.withWriter{ w =>
      eachLine{ line =>
        w.write(map(line))
        w.write(lineSeparator)
      }
    }

  def readLines: Seq[String] = withReader{ reader =>
    @tailrec
    def readLineRecurse(r: BufferedReader, lines: Seq[String]): Seq[String] =
      r.readLine() match {
        case null => lines
    case line => readLineRecurse(r, lines :+ line)
  }

    readLineRecurse(reader, Nil)
  }
}

class ReaderWrapper private (private[io] val reader: BufferedReader)
    extends ReaderWrapperLike with Closeable{

  override protected def getReader: BufferedReader = reader

  override def close(): Unit = reader.close()
}

object ReaderWrapper{

  def apply(reader: Reader): ReaderWrapper = reader match {
    case br: BufferedReader => new ReaderWrapper(br)
    case _ => new ReaderWrapper(new BufferedReader(reader))
  }

  def apply(path: Path): ReaderWrapper = new ReaderWrapper(Files.newBufferedReader(path))
  def apply(file: File): ReaderWrapper = apply(file.toPath)
}