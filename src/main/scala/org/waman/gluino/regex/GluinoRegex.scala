package org.waman.gluino.regex

import java.util.regex._

trait GluinoRegex {

  implicit def convertToStringWrapper(s: String): StringWrapper = new StringWrapper(s)

  class StringWrapper(s: String){

    def ==~(regex: String): Boolean = ==~(Pattern.compile(regex))

    def ==~(regex: Pattern): Boolean = regex.matcher(s).matches()

    def !=~(regex: String): Boolean = !=~(Pattern.compile(regex))

    def !=~(regex: Pattern): Boolean = !regex.matcher(s).matches()
  }
}

object GluinoRegex extends GluinoRegex