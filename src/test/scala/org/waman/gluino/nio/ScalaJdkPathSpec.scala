package org.waman.gluino.nio

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Files, Paths}

import org.scalatest.{Matchers, FlatSpec}
import scala.collection.JavaConversions._
import scala.collection.mutable

class ScalaJdkPathSpec extends FlatSpec with Matchers with GluinoPath{

  "BufferedReader" should "append to path" in {
    // setup
    val target = Files.createTempFile(null, null)
    Files.write(target, List("first line.", "second line."))

    val append = Files.createTempFile(null, null)
    Files.write(append, List("third line."))
    val reader = append.newBufferedReader()

    // exercise
    target << reader

    // verify
    Files.readAllLines(target) should contain theSameElementsInOrderAs
      List("first line.", "second line.", "third line.")
  }

  "append(Array[Byte])" should "write bytes to Writer with UTF-8 encoding" in {
    // setup
    val bytes: Array[Byte] = UTF_8.encode("gluino").array()
    val target = Files.createTempFile(null, null)

    // exercise
    target << bytes

    // verify
    Files.readAllLines(target, UTF_8)(0) shouldBe "gluino"
  }

  "foreachLine" should "iterate lines of the specified file content" in {
    // setup
    val path = Files.createTempFile(null, null)
    Files.write(path, List("first line.", "second line.", "third line."))
    var lines = mutable.ListBuffer[String]()

    // exercise
    path.eachLine(lines += _)

    // verify
    lines should contain theSameElementsInOrderAs
      List("first line.", "second line.", "third line.")
  }

  "Files#newInputStream" should "not return BufferedInputStream" in {
    val inputStream = Files.newInputStream(Paths.get("./build.sbt"))

    inputStream should not be a [java.io.BufferedInputStream]
  }
}
