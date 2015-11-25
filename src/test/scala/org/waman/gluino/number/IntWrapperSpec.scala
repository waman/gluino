package org.waman.gluino.number

class IntWrapperSpec extends GluinoNumberCustomSpec with GluinoNumber{

  "times(Int => Unit) method should" - {

    "execute the arg function this many times" in {
      __SetUp__
      var count = 0
      __Exercise__
      5.times{ count += 1 }
      __Verify__
      count should equal (5)
    }
  }
}
