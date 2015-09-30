package org.waman.gluino.regex

import org.scalatest.{FlatSpec, Matchers}

class GluinoRegexSpec extends FlatSpec with Matchers with GluinoRegex{

  // Pattern Operators
  "==~" should "return true for 'aaab' with the regex 'a*b'" in {
    ("aaab" ==~ "a*b") shouldBe true
  }

  it should "return false for 'aaaba' with the regex 'a*b'" in {
    ("aaaba" ==~ "a*b") shouldBe false
  }

  "!=~" should "return false for 'aaab' with the regex 'a*b'" in {
    ("aaab" !=~ "a*b") shouldBe false
  }

  it should "return true for 'aaaba' with the regex 'a*b'" in {
    ("aaaba" !=~ "a*b") shouldBe true
  }
}
