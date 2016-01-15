package org.waman.gluino.nio

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.nio.file._

import org.scalatest.OptionValues._
import org.waman.gluino.io.FileWrapperLikeSpec
import org.waman.gluino.nio.GluinoPath._

class PathWrapperSpec extends FileWrapperLikeSpec[Path, PathWrapper]{

  override protected def wrap(path: Path): PathWrapper = new PathWrapper(path)
  override protected def asF(path: Path): Path = path
  override protected def newFileWrapperLike(path: Path): PathWrapper = new PathWrapper(path)

  trait PathOperationFixture{
    val path: Path = Paths.get("path/to/some/dir")
    lazy val expectedChild: Path = Paths.get("path/to/some/dir/child.txt")
  }

  "/" - {
    "resolve a child path with the specified String" in new PathOperationFixture {
      __Exercise__
      val child = path / "child.txt"
      __Verify__
      child should equal(expectedChild)
    }

    "resolve a child path with the specified Path" in new PathOperationFixture {
      __Exercise__
      val child = path / Paths.get("child.txt")
      __Verify__
      child should equal(expectedChild)
    }
  }

  "\\" - {
    "resolve a child path with the specified String" in new PathOperationFixture {
      __Exercise__
      val child = path \ "child.txt"
      __Verify__
      child should equal(expectedChild)
    }

    "resolve a child path with the specified Path" in new PathOperationFixture {
      __Exercise__
      val child = path \ Paths.get("child.txt")
      __Verify__
      child should equal(expectedChild)
    }
  }

  "***** User Defined File Attribute *****" - {

    def assumeFileSystemSupportsUserDefinedFileAttribute(): Unit =
      assume(FileSystems.getDefault.supportedFileAttributeViews() contains "user")

    "getUserDefinedFileAttribute(String, Charset) method should" - {

      "return a value of a user-defined file attribute as String" in new FileFixture {
        assumeFileSystemSupportsUserDefinedFileAttribute()
        __SetUp__
        val byteBuffer = ByteBuffer.wrap("倭マン".getBytes(ISO2022))
        Files.setAttribute(path, "user:author", byteBuffer)
        __Exercise__
        val value = path.getUserDefinedFileAttribute("author", ISO2022)
        __Verify__
        value should equal ("倭マン")
      }

      """return a value of a user-defined file attribute as String with UTF-8 encoding
        | if the charset arg is omitted""".stripMargin in
        new FileFixture {
          assumeFileSystemSupportsUserDefinedFileAttribute()
          __SetUp__
          val byteBuffer = ByteBuffer.wrap("倭マン".getBytes(StandardCharsets.UTF_8))
          Files.setAttribute(path, "user:author", byteBuffer)
          __Exercise__
          val value = path.getUserDefinedFileAttribute("author")
          __Verify__
          value should equal ("倭マン")
        }
    }

    "setUserDefinedFileAttribute(String, String, Charset) method should" - {

      "set the specified attribute to the specified String" in new FileFixture {
        assumeFileSystemSupportsUserDefinedFileAttribute()
        __Exercise__
        path.setUserDefinedFileAttribute("author", "倭マン", ISO2022)
        __Verify__
        val bytes = Files.getAttribute(path, "user:author").asInstanceOf[Array[Byte]]
        val s = new String(bytes, ISO2022)
        s should equal ("倭マン")
      }

      """set the specified attribute to the specified String with UTF-8 encoding
        | if the charset arg is omitted""".stripMargin in
        new FileFixture {
          assumeFileSystemSupportsUserDefinedFileAttribute()
          __Exercise__
          path.setUserDefinedFileAttribute("author", "倭マン")
          __Verify__
          val bytes = Files.getAttribute(path, "user:author").asInstanceOf[Array[Byte]]
          val s = new String(bytes, StandardCharsets.UTF_8)
          s should equal ("倭マン")
        }
    }
  }

  //********** More Specific Type of Exception Conformations **********
  "***** File Operations for PathWrapper *****" - {

    "createFile() method should" - {

      "RETURN an Option[FileAlreadyExistsException] if the file already exist" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.createFile()
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
        }
    }

    "createDirectory() method should" - {

      "RETURN an Option[FileAlreadyExistsException] if the directory already exist" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.createDirectory()
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
        }
    }

    "renameTo() method should" - {

      // for files
      """RETURN an Option[FileAlreadyExistsException] if the target file exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.renameTo(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          path should exist
        }

      """RETURN an Option[FileAlreadyExistsException] if the target file exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.renameTo(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          path should exist
        }

      // for directories
      """RETURN an Option[FileAlreadyExistsException] if the target directory exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.renameTo(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      """RETURN an Option[FileAlreadyExistsException] if the target directory exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.renameTo(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      """RETURN an Option[DirectoryNotEmptyException] if the 'isOverride' arg is true
        | and the target directory is not empty""".stripMargin in
        new FileWrapperLike_NotEmptyDirectoryFixture {
          __SetUp__
          val target = GluinoPath.createTempDirectory(deleteOnExit = true)
          GluinoPath.createTempFile(target, deleteOnExit = true)
          __Exercise__
          val result = sut.renameTo(target, isOverride = true)
          __Verify__
          result.value should be (a [DirectoryNotEmptyException])
          dir should exist
        }
    }

    "move() method should" - {

      // for files
      """RETURN an Option[FileAlreadyExistsException] if the target file exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.move(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          path should exist
        }

      """RETURN an Option[FileAlreadyExistsException] if the target file exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.move(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          path should exist
        }

      // for directories
      """RETURN an Option[FileAlreadyExistsException] if the target directory exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.move(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      """RETURN an Option[FileAlreadyExistsException] if the target directory exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.move(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      """RETURN an Option[DirectoryNotEmptyException] if the 'isOverride' arg is true
        | and the target directory is not empty""".stripMargin in
        new FileWrapperLike_NotEmptyDirectoryFixture {
          __SetUp__
          val target = GluinoPath.createTempDirectory(deleteOnExit = true)
          GluinoPath.createTempFile(target, deleteOnExit = true)
          __Exercise__
          val result = sut.move(target, isOverride = true)
          __Verify__
          result.value should be (a [DirectoryNotEmptyException])
          dir should exist
        }
    }

    "copy() method should" - {

      // for files
      """RETURN an Option[FileAlreadyExistsException] if the target file exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
        }

      """RETURN an Option[FileAlreadyExistsException] if the target file exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
        }

      // for directories
      """RETURN an Option[FileAlreadyExistsException] if the target directory exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      """RETURN an Option[FileAlreadyExistsException] if the target directory exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      """RETURN an Option[DirectoryNotEmptyException] if the 'isOverride' arg is true
        | and the target directory is not empty""".stripMargin in
        new FileWrapperLike_NotEmptyDirectoryFixture {
          __SetUp__
          val target = GluinoPath.createTempDirectory(deleteOnExit = true)
          GluinoPath.createTempFile(target, deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target, isOverride = true)
          __Verify__
          result.value should be (a [DirectoryNotEmptyException])
          dir should exist
        }
    }

    "delete() method should" - {

      "RETURN an Option[NoSuchFileException] if the file does not exist" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          Files.delete(path)
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [NoSuchFileException])
        }

      "RETURN an Option[DirectoryNotEmptyException] if the directory is not empty" in
        new FileWrapperLike_NotEmptyDirectoryFixture {
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [DirectoryNotEmptyException])
        }
    }
  }

  "***** Directory Operation *****" - {

    "moveDir() method should" - {

      """RETURN an Option[FileAlreadyExistsException] if the target directory already exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.moveDir(target)
          __Verify__
          result.value should be (an [FileAlreadyExistsException])
        }

      """RETURN an Option[FileAlreadyExistsException] if the target directory already exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.moveDir(target, isOverride = false)
          __Verify__
          result.value should be (an [FileAlreadyExistsException])
        }

      "RETURN an Option[NotDirectoryException] if this is a file (not a directory)" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.deleteDir()
          __Verify__
          result.value should be (a [NotDirectoryException])
        }
    }

    "copyDir() method should" - {

      """RETURN an Option[FileAlreadyExistsException] if the target directory already exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copyDir(target)
          __Verify__
          result.value should be (an [FileAlreadyExistsException])
        }

      """RETURN an Option[FileAlreadyExistsException] if the target directory already exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copyDir(target, isOverride = false)
          __Verify__
          result.value should be (an [FileAlreadyExistsException])
        }

      "RETURN an Option[NotDirectoryException] if this is a file (not a directory)" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.deleteDir()
          __Verify__
          result.value should be (a [NotDirectoryException])
        }
    }

    "deleteDir() method should" - {

      "RETURN an Option[NotDirectoryException] if this is a file (not a directory)" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.deleteDir()
          __Verify__
          result.value should be (a [NotDirectoryException])
        }
    }
  }
}
