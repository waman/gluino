package org.waman.gluino.nio

import java.nio.file.FileSystems

import java.{util => jcf}

import scala.language.implicitConversions
import scala.language.reflectiveCalls

import java.nio.file.attribute._
import java.time.{Instant, ZoneId, ZonedDateTime}

import scala.collection.JavaConversions._
import java.nio.file.attribute.AclEntryPermission._

trait AttributeConverter {

  //***** Utilities for file attributes *****
  // POSIX
  def posix(s: String): FileAttribute[java.util.Set[PosixFilePermission]] =
    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(s))

  // ACL
  private case class FileAttributeImpl[T](name: String, value: T) extends FileAttribute[T]
  private def newAclAttr(entries: Array[AclEntry]): FileAttribute[jcf.List[AclEntry]] =
    new FileAttributeImpl[jcf.List[AclEntry]]("acl:acl", jcf.Arrays.asList(entries:_*))

  private val lookupService = FileSystems.getDefault.getUserPrincipalLookupService
  private val userAcl = """u(?:ser)?:(\w+):([r-][w-][x-])""".r
  private val groupAcl = """g(?:roup)?:(\w+):([r-][w-][x-])""".r

  lazy val permissionMap: Map[String, jcf.Set[AclEntryPermission]] = {
    val r = Set(READ_ACL, READ_NAMED_ATTRS, READ_DATA, SYNCHRONIZE)
    val w = Set(READ_ACL, READ_ATTRIBUTES, WRITE_ATTRIBUTES, WRITE_NAMED_ATTRS, WRITE_DATA, APPEND_DATA, SYNCHRONIZE)
    val x = Set(READ_ACL, READ_ATTRIBUTES, EXECUTE, SYNCHRONIZE)
    val wx = (w ++ x) + DELETE_CHILD
    Map(
      "---" -> Set(READ_ACL, READ_ATTRIBUTES),
      "r--" -> r,
      "-w-" -> w,
      "--x" -> x,
      "rw-" -> (r ++ w),
      "r-x" -> (r ++ x),
      "-wx" -> wx,
      "rwx" -> (r ++ wx)
    )
  }

  def acl(s: String): FileAttribute[jcf.List[AclEntry]] = {
    val entryList = for(e <- s.split(",")) yield convertStringToAclEntry(e.trim)
    newAclAttr(entryList)
  }

  private def convertStringToAclEntry(s: String): AclEntry = {

    def toAclEntry(principal: UserPrincipal, rwx: String): AclEntry = {
      AclEntry.newBuilder()
        .setPrincipal(principal)
        .setPermissions(permissionMap(rwx))
        .setType(AclEntryType.ALLOW).build()
    }

    s match {
      case userAcl(userName, rwx) =>
        val user = lookupService.lookupPrincipalByName(userName)
        toAclEntry(user, rwx)
      case groupAcl(groupName, rwx) =>
        val group = lookupService.lookupPrincipalByGroupName(groupName)
        toAclEntry(group, rwx)
    }
  }

  //***** FileTime conversions *****
  // java.time.Instant
  implicit def convertFileTimeToInstant(fileTime: FileTime): Instant = fileTime.toInstant
  implicit def convertInstantToFileTime(instant: Instant): FileTime = FileTime.from(instant)

  // ZonedDateTime/OffsetDateTime
  implicit def convertFileTimeToZonedDateTime(fileTime: FileTime): ZonedDateTime =
    convertFileTimeToInstant(fileTime).atZone(ZoneId.systemDefault())

//  implicit def convertZonedDateTimeToFileTime(zdt: ZonedDateTime): FileTime =
//    convertInstantToFileTime(zdt.toInstant)
//
//  implicit def convertOffsetDateTimeToFileTime(odt: OffsetDateTime): FileTime =
//    convertInstantToFileTime(odt.toInstant)

  implicit def convertInstantialToFileTime(instantial: { def toInstant(): Instant}): FileTime =
    convertInstantToFileTime(instantial.toInstant())

  // java.util.Date
  implicit def convertFileTimeToDate(fileTime: FileTime): java.util.Date =
    new java.util.Date(fileTime.toMillis)

  implicit def convertDateToFileTime(date: java.util.Date): FileTime =
    FileTime.fromMillis(date.getTime)
}
