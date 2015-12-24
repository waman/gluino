package org.waman.gluino.nio

import java.nio.file.FileSystems
import java.nio.file.attribute._
import java.nio.file.attribute.PosixFilePermission._
import java.nio.file.attribute.AclEntryPermission._
import java.nio.file.attribute.AclEntryType._
import java.nio.file.attribute.AclEntryFlag._
import java.time.{Instant, OffsetDateTime, ZoneId, ZonedDateTime}
import java.{util => jcf}

import org.waman.gluino.io.GluinoIOCustomSpec

import org.scalatest.LoneElement._
import org.waman.scalatest_util.{ImplicitConversion, WindowsSpecific}

class AttributeConverterSpec extends GluinoIOCustomSpec with AttributeConverter{

  def name(entry: FileAttribute[jcf.List[AclEntry]]) = entry.value().loneElement.principal().getName
  def name(entry: FileAttribute[jcf.List[AclEntry]], i: Int) = entry.value().get(i).principal().getName

  def permissions(entry: FileAttribute[jcf.List[AclEntry]]) = entry.value().loneElement.permissions()
  def permissions(entry: FileAttribute[jcf.List[AclEntry]], i: Int) = entry.value().get(i).permissions()

  def aclType(entry: FileAttribute[jcf.List[AclEntry]]) = entry.value().loneElement.`type`()
  def flags(entry: FileAttribute[jcf.List[AclEntry]]) = entry.value().loneElement.flags()

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

    "acl(UserPrincipal, ...) method should" - {

      val lookupService = FileSystems.getDefault.getUserPrincipalLookupService
      def getUser(name: String) = lookupService.lookupPrincipalByName(name)

      "create FileAttribute of ACL without AclEntryFlag" taggedAs WindowsSpecific in
        new WindowsAclRequirement {
          __SetUp__
          val guest = getUser("Guest")
          __Exercise__
          val sut: FileAttribute[jcf.List[AclEntry]] = acl(guest, Set(READ_ACL, READ_ATTRIBUTES), ALLOW)
          __Verify__
          name(sut) should fullyMatch regex """.+\\Guest"""
          permissions(sut) should contain theSameElementsAs Set(READ_ACL, READ_ATTRIBUTES)
          aclType(sut) should equal (ALLOW)
          flags(sut) should be (empty)
        }

      "create FileAttribute of ACL with AclEntryFlag" taggedAs WindowsSpecific in
        new WindowsAclRequirement {
          __SetUp__
          val guest = getUser("Guest")
          __Exercise__
          val sut: FileAttribute[jcf.List[AclEntry]] =
            acl(guest, Set(READ_ACL, READ_ATTRIBUTES), ALLOW, INHERIT_ONLY)
          __Verify__
          name(sut) should fullyMatch regex """.+\\Guest"""
          permissions(sut) should contain theSameElementsAs Set(READ_ACL, READ_ATTRIBUTES)
          aclType(sut) should equal (ALLOW)
          flags(sut) should contain theSameElementsAs Set(INHERIT_ONLY)
        }
    }

    "acl(String) method should" - {

      "(User/Group)" - {

        // User
        "create ACL file attribute for 'Guest' user by a string 'u:Guest:---'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut: FileAttribute[jcf.List[AclEntry]] = acl("u:Guest:---")
            __Verify__
            name(sut) should fullyMatch regex """.+\\Guest"""
          }

        "create ACL file attribute for 'Guest' user by a string 'user:Guest:---'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut: FileAttribute[jcf.List[AclEntry]] = acl("user:Guest:---")
            __Verify__
            name(sut) should fullyMatch regex """.+\\Guest"""
          }

        "create ACL file attributes for users by a string 'u:Guest:---, u:{user}:---'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __SetUp__
            val me = System.getProperty("user.name")
            __Exercise__
            val sut = acl(s"u:Guest:---, u:$me:---")
            __Verify__
            sut.value() should have size 2
            name(sut, 0) should fullyMatch regex """.+\\Guest"""
            name(sut, 1) should fullyMatch regex s""".+\\\\$me"""
          }

        // Group
        "create ACL file attribute for 'Guests' group by a string 'g:Guests:---'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut: FileAttribute[jcf.List[AclEntry]] = acl("g:Guests:---")
            __Verify__
            name(sut) should fullyMatch regex """.+\\Guests"""
          }

        "create ACL file attribute for 'Guests' Group by a string 'group:Guests:---'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut: FileAttribute[jcf.List[AclEntry]] = acl("group:Guests:---")
            __Verify__
            name(sut) should fullyMatch regex """.+\\Guests"""
          }

        "create ACL file attributes for user and group by a string 'u:Guest:---, g:Guests:---'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:---, g:Guests:---")
            __Verify__
            sut.value() should have size 2
            name(sut, 0) should fullyMatch regex """.+\\Guest"""
            name(sut, 1) should fullyMatch regex """.+\\Guests"""
          }
      }

      "(Permission)" - {

        val r__ = Set(READ_ACL, READ_NAMED_ATTRS, READ_DATA, SYNCHRONIZE)
        val _w_ = Set(READ_ACL, READ_ATTRIBUTES, WRITE_ATTRIBUTES, WRITE_NAMED_ATTRS, WRITE_DATA, APPEND_DATA, SYNCHRONIZE)
        val __x = Set(READ_ACL, READ_ATTRIBUTES, EXECUTE, SYNCHRONIZE)

        "create ACL file attributes with permissions '---'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:---")
            __Verify__
            permissions(sut) should contain theSameElementsAs Set(READ_ACL, READ_ATTRIBUTES)
          }

        "create ACL file attributes with permissions 'r--'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:r--")
            __Verify__
            permissions(sut) should contain theSameElementsAs r__
          }

        "create ACL file attributes with permissions '-w-'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:-w-")
            __Verify__
            permissions(sut) should contain theSameElementsAs _w_
          }

        "create ACL file attributes with permissions '--x'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:--x")
            __Verify__
            permissions(sut) should contain theSameElementsAs __x
          }

        "create ACL file attributes with permissions 'rw-'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:rw-")
            __Verify__
            permissions(sut) should contain theSameElementsAs (r__ ++ _w_)
          }

        "create ACL file attributes with permissions 'r-x'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:r-x")
            __Verify__
            permissions(sut) should contain theSameElementsAs (r__ ++ __x)
          }

        "create ACL file attributes with permissions '-wx'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:-wx")
            __Verify__
            permissions(sut) should contain theSameElementsAs ((_w_ ++ __x) + DELETE_CHILD)
          }

        "create ACL file attributes with permissions 'rwx'" taggedAs WindowsSpecific in
          new WindowsAclRequirement {
            __Exercise__
            val sut = acl("u:Guest:rwx")
            __Verify__
            permissions(sut) should contain theSameElementsAs ((r__ ++ _w_ ++ __x) + DELETE_CHILD)
          }
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

      "should implicitly convert to FileTime" taggedAs ImplicitConversion ignore {
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

      "should implicitly convert to FileTime" taggedAs ImplicitConversion ignore {
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

      "should implicitly convert to FileTime" taggedAs ImplicitConversion ignore {
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

      "should implicitly convert to FileTime" taggedAs ImplicitConversion ignore new DateFixture{
        __Verify__
        noException should be thrownBy{
          convertImplicitly[FileTime](date)
        }
      }
    }
  }
}
