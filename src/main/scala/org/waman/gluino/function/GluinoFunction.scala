package org.waman.gluino.function

import scala.language.implicitConversions
import scala.collection.JavaConversions._
import java.util.{function => juf}
import java.util.{stream => jus}

trait GluinoFunction {

  //***** Conversion of java.util.Stream to Stream *****
  implicit def convertJavaStreamToStream[E](stream: jus.Stream[E]): Stream[E] =
    stream.iterator.toStream


  //***** Conversion between java.util.function and Scala function *****
  implicit def convertJavaFunctionToFunction[A, R](f: juf.Function[A, R]): A => R = f.apply

  implicit def convertFunctionToJavaFunction[A, R](f: A => R): juf.Function[A, R] =
    new juf.Function[A, R](){
      override def apply(a: A): R = f(a)
    }

  implicit def convertBiPredicateToFunction[A, B](pred: juf.BiPredicate[A, B]): (A, B) => Boolean = pred.test

  implicit def convertFunctionToBiPredicate[A, B](f: (A, B) => Boolean): juf.BiPredicate[A, B] =
    new juf.BiPredicate[A, B](){
      override def test(a: A, b: B): Boolean = f(a, b)
    }
}

object GluinoFunction extends GluinoFunction
