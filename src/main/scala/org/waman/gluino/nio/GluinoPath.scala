package org.waman.gluino.nio

import java.net.URI
import java.nio.file._
import java.nio.file.attribute._
import java.time.temporal.TemporalAccessor
import java.time.{OffsetDateTime, Instant, ZoneId, ZonedDateTime}

trait GluinoPath {

  val tmpdir: Path = Paths.get(System.getProperty("java.io.tmpdir"))
  
  def createTempFile
    (dir: Path = tmpdir, prefix: String = null, suffix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createTempFile(dir, prefix, suffix, attributes.toArray:_*)
  def createTempDirectory
    (dir: Path = tmpdir, prefix: String = null, attributes: Set[FileAttribute[_]] = Set()): Path =
    Files.createTempDirectory(dir, prefix, attributes.toArray:_*)

  //***** Path Creation *****
  implicit def convertStringToPath(path: String): Path = Paths.get(path)
  implicit def convertSeqToPath(path: Seq[String]): Path = Paths.get(path.head, path.tail:_*)
  implicit def convertUriToPath(uri: URI): Path = Paths.get(uri)

  //***** Path Operation *****
  implicit def convertToPathWrapper(path: Path): PathWrapper = new PathWrapper(path)

  //***** Methods defined at Files *****
  implicit def convertToFilesCategory(path: Path): FilesCategory = new FilesCategory(path)

  //***** Utilities for file attributes *****
  implicit def convertStringToPosixFilePermissionSet(s: String): FileAttribute[java.util.Set[PosixFilePermission]] =
    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(s))

  //***** FileTime conversions *****
  implicit def convertFileTimeToInstant(fileTime: FileTime): Instant = fileTime.toInstant
  implicit def convertFileTimeToZonedDateTime(fileTime: FileTime): ZonedDateTime =
    convertFileTimeToInstant(fileTime).atZone(ZoneId.systemDefault())
  implicit def convertInstantToFileTime(instant: Instant): FileTime =
    FileTime.from(instant)
  implicit def convertZonedDateTimeToFileTime(zdt: ZonedDateTime): FileTime =
    convertInstantToFileTime(zdt.toInstant)
  implicit def convertOffsetDateTimeToFileTime(odt: OffsetDateTime): FileTime =
    convertInstantToFileTime(odt.toInstant)

  implicit def convertFileTimeToDate(fileTime: FileTime): java.util.Date =
    new java.util.Date(fileTime.toMillis)
  implicit def convertDateToFileTime(date: java.util.Date): FileTime =
    FileTime.fromMillis(date.getTime)
}

object GluinoPath extends GluinoPath