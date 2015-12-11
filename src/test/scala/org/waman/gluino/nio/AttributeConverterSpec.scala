package org.waman.gluino.nio

import java.nio.file.attribute.{AclEntry, FileAttribute, FileTime}
import java.nio.file.attribute.PosixFilePermission._
import java.time.{Instant, OffsetDateTime, ZoneId, ZonedDateTime}
import java.{util => jcf}

import org.waman.gluino.{WindowsAssumption, WindowsSpecific, ImplicitConversion}
import org.waman.gluino.io.GluinoIOCustomSpec

import org.scalatest.LoneElement._

class AttributeConverterSpec extends GluinoIOCustomSpec with AttributeConverter{

  "***** FileAttribute *****" - {

    "posix(String) method should" - {

      "convert String 'r--------' to a file attribute of java.util.Set of OWNER_READ" in {
        __Exercise__
        val sut = posix("r--------")
        __Verify__
        sut.value().loneElement should be (OWNER_READ)
      }

      "convert String '-w-------' to a file attribute of java.util.Set of OWNER_WRITE" in {
        __Exercise__
        val sut = posix("-w-------")
        __Verify__
        sut.value().loneElement should be (OWNER_WRITE)
      }

      "convert String '--x------' to a file attribute of java.util.Set of OWNER_EXECUTE" in {
        __Exercise__
        val sut = posix("--x------")
        __Verify__
        sut.value().loneElement should be (OWNER_EXECUTE)
      }

      "convert String '---r-----' to a file attribute of java.util.Set of GROUP_READ" in {
        __Exercise__
        val sut = posix("---r-----")
        __Verify__
        sut.value().loneElement should be (GROUP_READ)
      }

      "convert String '----w----' to a file attribute of java.util.Set of GROUP_WRITE" in {
        __Exercise__
        val sut = posix("----w----")
        __Verify__
        sut.value().loneElement should be (GROUP_WRITE)
      }

      "convert String '-----x---' to a file attribute of java.util.Set of GROUP_EXECUTE" in {
        __Exercise__
        val sut = posix("-----x---")
        __Verify__
        sut.value().loneElement should be (GROUP_EXECUTE)
      }

      "convert String '------r--' to a file attribute of java.util.Set of OTHERS_READ" in {
        __Exercise__
        val sut = posix("------r--")
        __Verify__
        sut.value().loneElement should be (OTHERS_READ)
      }

      "convert String '-------w-' to a file attribute of java.util.Set of OTHERS_READ" in {
        __Exercise__
        val sut = posix("-------w-")
        __Verify__
        sut.value().loneElement should be (OTHERS_WRITE)
      }

      "convert String '--------x' to a file attribute of java.util.Set of OTHERS_READ" in {
        __Exercise__
        val sut = posix("--------x")
        __Verify__
        sut.value().loneElement should be (OTHERS_EXECUTE)
      }

      "convert String 'rwx------' to a file attribute of java.util.Set of OWNER_READ, OWNER_WRITE and OWNER_EXECUTE" in {
        __Exercise__
        val sut = posix("rwx------")
        __Verify__
        sut.value() should contain theSameElementsAs Set(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE)
      }
    }

    "acl(String) method should" - {

      // User
      "create ACL file attribute for Guest user by a string 'u:Guest:---'" taggedAs WindowsSpecific in
        new WindowsAssumption {
          __Exercise__
          val sut: FileAttribute[jcf.List[AclEntry]] = acl("u:Guest:---")
          __Verify__
          sut.value().loneElement.principal().getName should fullyMatch regex """.+\\Guest"""
        }

      "create ACL file attribute for Guest user by a string 'user:Guest:---'" taggedAs WindowsSpecific in
        new WindowsAssumption {
          __Exercise__
          val sut: FileAttribute[jcf.List[AclEntry]] = acl("user:Guest:---")
          __Verify__
          sut.value().loneElement.principal().getName should fullyMatch regex """.+\\Guest"""
        }

      "create ACL file attributes for users by a string 'u:Guest:---, u:{user}:---'" taggedAs WindowsSpecific in
        new WindowsAssumption {
          __SetUp__
          val me = System.getProperty("user.name")
          __Exercise__
          val sut = acl(s"u:Guest:---, u:$me:---")
          __Verify__
          sut.value() should have size 2
          sut.value().get(0).principal().getName should fullyMatch regex """.+\\Guest"""
          sut.value().get(1).principal().getName should fullyMatch regex s""".+\\\\$me"""
        }
    }
  }

  "***** FileTime conversions *****" - {
    val fileTime = FileTime.fromMillis(1234567890L)
    val instant = Instant.ofEpochMilli(1234567890L)
    val zdt0 = ZonedDateTime.parse("1970-01-15T06:56:07.89Z[Greenwich]")
    val zdt  = ZonedDateTime.parse("1970-01-15T15:56:07.890+09:00[Asia/Tokyo]")
    val odt = OffsetDateTime.parse("1970-01-15T06:56:07.89Z")

    trait DateFixture{
      val date = new java.util.Date(1234567890L)
    }

    "Instant" - {
      "from FileTime by convertFileTimeToInstant()" in {
        __Exercise__
        val sut = convertFileTimeToInstant(fileTime)
        __Verify__
        sut should equal (instant)
      }

      "to FileTime by convertInstantToFileTime()" in {
        __Exercise__
        val sut = convertInstantToFileTime(instant)
        __Verify__
        sut should equal (fileTime)
      }

      "should implicitly convert to FileTime" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[FileTime](instant)
        }
      }
    }

    "ZonedDateTime" - {
      "from FileTime by convertFileTimeToZonedDateTime() which uses system default timezone" in {
        __Exercise__
        val sut = convertFileTimeToZonedDateTime(fileTime)
        __Verify__
        sut should equal (ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()))
      }

      "to FileTime by convertInstantialToFileTime() with Greenwich timezone" in {
        __Exercise__
        val sut = convertInstantialToFileTime(zdt0)
        __Verify__
        sut should equal (fileTime)
      }

      "to FileTime by convertInstantialToFileTime() with Asia/Tokyo timezone" in {
        __Exercise__
        val sut = convertInstantialToFileTime(zdt)
        __Verify__
        sut should equal (fileTime)
      }

      "should implicitly convert to FileTime" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[FileTime](zdt)
        }
      }
    }

    "OffsetDateTime" - {
      "to FileTime by convertInstantialToFileTime()" in {
        __Exercise__
        val sut = convertInstantialToFileTime(odt)
        __Verify__
        sut should equal (fileTime)
      }

      "should implicitly convert to FileTime" taggedAs ImplicitConversion in {
        __Verify__
        noException should be thrownBy {
          convertImplicitly[FileTime](odt)
        }
      }
    }

    "Date" - {
      "from FileTime by convertFileTimeToDate()" in new DateFixture{
        __Exercise__
        val sut = convertFileTimeToDate(fileTime)
        __Verify__
        sut should equal (date)
      }

      "to FileTime by convertDateToFileTime()" in new DateFixture{
        __Exercise__
        val sut = convertDateToFileTime(date)
        __Verify__
        sut should equal (fileTime)
      }

      "should implicitly convert to FileTime" taggedAs ImplicitConversion in new DateFixture{
        __Verify__
        noException should be thrownBy{
          convertImplicitly[FileTime](date)
        }
      }
    }
  }
}
