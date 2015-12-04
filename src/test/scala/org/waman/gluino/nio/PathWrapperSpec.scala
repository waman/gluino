package org.waman.gluino.nio

import java.nio.file._

import org.scalatest.OptionValues._
import org.waman.gluino.io.FileWrapperLikeSpec

class PathWrapperSpec
    extends FileWrapperLikeSpec[Path, PathWrapper]
    with GluinoPath {

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

  "***** File Operations for PathWrapper *****" - {

    "renameTo() method should" - {

      // for files
      "RETURN an Option[FileAlreadyExistsException] if the target file exists and the 'isOverride' arg is omitted" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.renameTo(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          path should exist
        }

      "RETURN an Option[FileAlreadyExistsException] if the target file exists and the 'isOverride' arg is false" in
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
      "RETURN an Option[FileAlreadyExistsException] if the target directory exists and the 'isOverride' arg is omitted" in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.renameTo(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      "RETURN an Option[FileAlreadyExistsException] if the target directory exists and the 'isOverride' arg is false" in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.renameTo(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      "RETURN an Option[DirectoryNotEmptyException] if the 'isOverride' arg is true and the target directory is not empty" in
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
      "RETURN an Option[FileAlreadyExistsException] if the target file exists and the 'isOverride' arg is omitted" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.move(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          path should exist
        }

      "RETURN an Option[FileAlreadyExistsException] if the target file exists and the 'isOverride' arg is false" in
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
      "RETURN an Option[FileAlreadyExistsException] if the target directory exists and the 'isOverride' arg is omitted" in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.move(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      "RETURN an Option[FileAlreadyExistsException] if the target directory exists and the 'isOverride' arg is false" in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.move(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      "RETURN an Option[DirectoryNotEmptyException] if the 'isOverride' arg is true and the target directory is not empty" in
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
      "RETURN an Option[FileAlreadyExistsException] if the target file exists and the 'isOverride' arg is omitted" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
        }

      "RETURN an Option[FileAlreadyExistsException] if the target file exists and the 'isOverride' arg is false" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val target = createTempFile(deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
        }

      // for directories
      "RETURN an Option[FileAlreadyExistsException] if the target directory exists and the 'isOverride' arg is omitted" in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      "RETURN an Option[FileAlreadyExistsException] if the target directory exists and the 'isOverride' arg is false" in
        new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val target = createTempDirectory(deleteOnExit = true)
          __Exercise__
          val result = sut.copy(target, isOverride = false)
          __Verify__
          result.value should be (a [FileAlreadyExistsException])
          dir should exist
        }

      "RETURN an Option[DirectoryNotEmptyException] if the 'isOverride' arg is true and the target directory is not empty" in
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
}
