package org.waman.gluino.nio

import java.nio.file.attribute.{FileTime, PosixFilePermissions, PosixFilePermission, FileAttribute}
import java.time.{OffsetDateTime, ZoneId, ZonedDateTime, Instant}

trait AttributeConverter {

  //***** Utilities for file attributes *****
  implicit def convertStringToPosixFilePermissionSet(s: String): FileAttribute[java.util.Set[PosixFilePermission]] =
    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(s))


  //***** FileTime conversions *****
  // Instant
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
