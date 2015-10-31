package org.waman.gluino.function

import java.util.{function => juf}
import scala.collection.JavaConversions._

trait GluinoFunction {

  //***** Conversion of java.util.Stream to Stream *****
  implicit def convertJavaStreamToStream[E](stream: java.util.stream.Stream[E]): Stream[E] =
    stream.iterator.toStream


  //***** Conversion between java.util.function and Scala function *****
  implicit def convertBiPredicateToScalaFunction[A, B](pred: juf.BiPredicate[A, B]): (A, B) => Boolean = pred.test
  implicit def convertScalaFunctionToBiPredicate[A, B](f: (A, B) => Boolean): juf.BiPredicate[A, B] =
    new juf.BiPredicate[A, B](){
      override def test(a: A, b: B): Boolean = f(a, b)
    }
}

object GluinoFunction extends GluinoFunction
