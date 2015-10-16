package org.waman.gluino.function

import java.util.{function => juf}

trait GluinoFunction {

  implicit def convertBiPredicateToScalaFunction[A, B](pred: juf.BiPredicate[A, B]): (A, B) => Boolean = pred.test
  implicit def convertScalaFunctionToBiPredicate[A, B](f: (A, B) => Boolean): juf.BiPredicate[A, B] =
    new juf.BiPredicate[A, B](){
      override def test(a: A, b: B): Boolean = f(a, b)
    }
}

object GluinoFunction extends GluinoFunction
