package org.waman.gluino.regex

import scala.language.implicitConversions

import java.util.regex._

import scala.util.matching.Regex

trait GluinoRegex {

  implicit def wrapString(s: String): StringWrapper = new StringWrapper(s)

  class StringWrapper(s: String){

    //***** Pattern Operations *****
    def ==~(regex: String): Boolean = ==~(Pattern.compile(regex))

    def ==~(regex: Pattern): Boolean = regex.matcher(s).matches()

    def ==~(regex: Regex): Boolean = ==~(regex.regex)

    def !=~(regex: String): Boolean = !=~(Pattern.compile(regex))

    def !=~(regex: Pattern): Boolean = !regex.matcher(s).matches()

    def !=~(regex: Regex): Boolean = !=~(regex.regex)

    //***** Match Operator *****
    //def =~ (regex: String): Matcher = =~
  }
}

object GluinoRegex extends GluinoRegex