package org.waman.gluino.regex

import org.waman.gluino.GluinoCustomSpec

class GluinoRegexSpec extends GluinoCustomSpec with GluinoRegex{

  "***** Pattern Operators *****" - {

    "==~ should" - {
      "return true for 'aaab' with the regex 'a*b'" in {
        ("aaab" ==~ "a*b") shouldBe true
      }

      "return false for 'aaaba' with the regex 'a*b'" in {
        ("aaaba" ==~ "a*b") shouldBe false
      }
    }

    "!=~ should" - {

      "return false for 'aaab' with the regex 'a*b'" in {
        ("aaab" !=~ "a*b") shouldBe false
      }

      "return true for 'aaaba' with the regex 'a*b'" in {
        ("aaaba" !=~ "a*b") shouldBe true
      }
    }
  }
}
