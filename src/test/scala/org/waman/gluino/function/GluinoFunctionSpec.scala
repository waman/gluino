package org.waman.gluino.function


import java.util.{stream => jus, function => juf}

import org.waman.gluino.io.GluinoIOCustomSpec

class GluinoFunctionSpec extends GluinoIOCustomSpec with GluinoFunction{

  "convertJavaStreamToStream() method should convert java.util.stream.Stream to Stream of Scala" in {
    __SetUp__
    val js: jus.Stream[String] = jus.Stream.of("1st line.", "2nd line.", "3rd line.")
    __Exercise__
    val stream: Stream[String] = convertJavaStreamToStream(js)
    __Verify__
    stream should contain theSameElementsAs Seq("1st line.", "2nd line.", "3rd line.")
  }

  "convertJavaFunctionToFunction() method should convert java.util.function.Function to Function of Scala" in {
    __SetUp__
    val jf: juf.Function[String, Integer] = new juf.Function[String, Integer]{
      override def apply(s: String): Integer = s(0).toInt
    }
    __Exercise__
    val f: String => Integer = convertJavaFunctionToFunction(jf)
    __Verify__
    val result = Seq("1st line.", "2nd line.", "3rd line.").map(f)
    result should contain theSameElementsInOrderAs Seq(1, 2, 3)
  }
}
