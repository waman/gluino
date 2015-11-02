package org.waman.gluino.nio

import java.nio.file.attribute.FileTime
import java.nio.file.attribute.PosixFilePermission._
import java.time.{Instant, OffsetDateTime, ZoneId, ZonedDateTime}

import org.waman.gluino.GluinoCustomSpec

class AttributeConverterSpec extends GluinoCustomSpec with AttributeConverter{

  "***** POSIX File Permission *****" - {
    "convertStringToPosixFilePermissionSet() method should" - {
      "convert String to FileAttribute whose value is Set of PosixFilePermissions" in {
        __Exercise__
        val sut = convertStringToPosixFilePermissionSet("rwx------")
        __Verify__
        sut.value() should contain only(OWNER_READ, OWNER_WRITE, OWNER_EXECUTE)
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

      "should implicitly convert to FileTime" in new FileFixture {
        __Verify__
        "java.nio.file.Files.setLastModifiedTime(path, instant)" should compile
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

      "should implicitly convert to FileTime" in new FileFixture {
        __Verify__
        "java.nio.file.Files.setLastModifiedTime(path, zdt)" should compile
      }
    }

    "OffsetDateTime" - {
      "to FileTime by convertInstantialToFileTime()" in {
        __Exercise__
        val sut = convertInstantialToFileTime(odt)
        __Verify__
        sut should equal (fileTime)
      }

      "should implicitly convert to FileTime" in new FileFixture {
        __Verify__
        "java.nio.file.Files.setLastModifiedTime(path, odt)" should compile
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

      "should implicitly convert to FileTime" in new FileFixture {
        __SetUp__
        val date = new java.util.Date()
        __Verify__
        "java.nio.file.Files.setLastModifiedTime(path, date)" should compile
      }
    }
  }
}
