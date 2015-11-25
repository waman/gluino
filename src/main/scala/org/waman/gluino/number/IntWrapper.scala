package org.waman.gluino.number

import scala.annotation.tailrec

class IntWrapper(i: Int) {

  def times(consumer: => Unit): Unit = {
    @tailrec
    def consume(n: Int): Unit = n match {
      case 0 =>
      case _ if n > 0 =>
        consumer
        consume(n-1)
    }

    consume(i)
  }
}
