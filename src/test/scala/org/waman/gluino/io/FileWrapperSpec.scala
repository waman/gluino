package org.waman.gluino.io

import java.io.{File, IOException}
import java.nio.file.{Files, Path}

import scala.collection.JavaConversions._

import org.scalatest.OptionValues._
import org.waman.gluino.nio.GluinoPath
import org.waman.gluino.number.GluinoNumber

import scala.collection.mutable

trait FileWrapperLikeSpec[F, W <: FileWrapperLike[F, W]]
    extends InputStreamWrapperLikeSpec
    with OutputStreamWrapperLikeSpec[W]
    with GluinoNumber{

  protected def asF(path: Path): F
  protected def newFileWrapperLike(path: Path): W

  override protected def newInputStreamWrapperLike(path: Path) = newFileWrapperLike(path)
  override protected def newOutputStreamWrapperLike(path: Path) = newFileWrapperLike(path)

  // File Fixture
  trait FileWrapperLike_FileFixture extends FileFixture{
    val sut = newFileWrapperLike(path)
  }

  trait FileWrapperLike_FileWithContentFixture extends FileWithContentFixture{
    val sut = newFileWrapperLike(path)
  }

  trait FileWrapperLike_DirectoryFixture extends DirectoryFixture{
    val sut = newFileWrapperLike(dir)
  }

  // Directory Fixture
  trait FileWrapperLike_ReadOnlyDirFixture{
    val sut = newFileWrapperLike(readOnlyDir)
  }

  trait FileWrapperLike_NotEmptyDirectoryFixture extends NotEmptyDirectoryFixture{
    val sut = newFileWrapperLike(dir)
  }

  trait FileWrapperLike_DirectoryWithFilesFixture extends DirectoryWithFilesFixture{
    val sut = newFileWrapperLike(dir)
  }

  trait NotEmptyDirectoryTargetFixture{
    val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
    GluinoPath.createTempFile(targetPath, deleteOnExit = true)
    val target = asF(targetPath)
  }

  "***** File Operations *****" - {

    "renameTo() method should" - {

    }

    "move() method should" - {

    }

    "copy() method should" - {

      "for files" - {

        "copy a file" in new FileWrapperLike_FileWithContentFixture {
          __SetUp__
          val targetPath = createNotExistingFile()
          val target = asF(targetPath)
          __Exercise__
          sut.copy(target)
          __Verify__
          path should exist
          targetPath should exist
          text(path) should equal(contentAsString)
          text(targetPath) should equal(contentAsString)
        }

        "RETURN an Option[IOException] if the target file exists and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempFile(deleteOnExit = true))
            __Exercise__
            val result = sut.copy(target)
            __Verify__
            result.value should be(a[IOException])
          }

        "RETURN an Option[IOException] if the target file exists and the 'isOverride' arg is false" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempFile(deleteOnExit = true))
            __Exercise__
            val result = sut.copy(target, isOverride = false)
            __Verify__
            result.value should be(a[IOException])
          }

        "copy a file even if the target file exists when the 'isOverride' arg is true" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempFile(deleteOnExit = true)
            Files.write(targetPath, Seq("Some content."))
            val target = asF(targetPath)
            __Exercise__
            sut.copy(target, isOverride = true)
            __Verify__
            path should exist
            targetPath should exist
            text(path) should equal(contentAsString)
            text(targetPath) should equal(contentAsString)
          }

        "do nothing if the source and target are the same file and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val target = asF(path)
            __Exercise__
            val result = sut.copy(target)
            __Verify__
            result should be (None)
            path should exist
            text(path) should equal(contentAsString)
          }

        "do nothing if the source and target are the same file and the 'isOverride' arg is false" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val target = asF(path)
            __Exercise__
            val result = sut.copy(target, isOverride = false)
            __Verify__
            result should be (None)
            path should exist
            text(path) should equal(contentAsString)
          }

        "do nothing if the source and target are the same file and the 'isOverride' arg is true" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val target = asF(path)
            __Exercise__
            val result = sut.copy(target, isOverride = true)
            __Verify__
            result should be (None)
            path should exist
            text(path) should equal(contentAsString)
          }
      }

      "for directories" - {

        "copy the directory" in new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val targetPath = createNotExistingDirectory()
          val target = asF(targetPath)
          __Exercise__
          sut.copy(target)
          __Verify__
          dir should exist
          targetPath should exist
        }

        "RETURN an Option[IOException] if the target directory exists and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempDirectory(deleteOnExit = true))
            __Exercise__
            val result = sut.copy(target)
            __Verify__
            result.value should be (a [IOException])
          }

        "RETURN an Option[IOException] if the target directory exists and the 'isOverride' arg is false" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempDirectory(deleteOnExit = true))
            __Exercise__
            val result = sut.copy(target, isOverride = false)
            __Verify__
            result.value should be (a [IOException])
          }

        "copy the directory even if the target directory exists when the 'isOverride' arg is true" in
          new FileWrapperLike_DirectoryFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
            val target = asF(targetPath)
            __Exercise__
            sut.copy(target, isOverride = true)
            __Verify__
            dir should exist
            targetPath should exist
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is omitted" in
          new FileWrapperLike_NotEmptyDirectoryFixture{
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.copy(target)
              __Verify__
              result.value should be (a [IOException])
            }
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is false" in
          new FileWrapperLike_NotEmptyDirectoryFixture {
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.copy(target, isOverride = false)
              __Verify__
              result.value should be (a [IOException])
            }
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is omitted" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.copy(target)
            __Verify__
            result should be (None)
            dir should exist
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is false" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.copy(target, isOverride = false)
            __Verify__
            result should be (None)
            dir should exist
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is true" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.copy(target, isOverride = true)
            __Verify__
            result should be (None)
            dir should exist
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is true" in
          new FileWrapperLike_NotEmptyDirectoryFixture {
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.copy(target, isOverride = true)
              __Verify__
              result.value should be (a [IOException])
            }
          }
      }
    }

    "delete() method should" - {

      "delete a file" in new FileWrapperLike_FileFixture{
        __Exercise__
        val result = sut.delete()
        __Verify__
        path should not (exist)
        result should be (None)
      }

      "delete a directory" in new FileWrapperLike_DirectoryFixture {
        __Exercise__
        val result = sut.delete()
        __Verify__
        dir should not (exist)
        result should be (None)
      }

      "RETURN an Option[IOException] if the file does not exist" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          Files.delete(path)
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [IOException])
        }

      "RETURN an Option[IOException] if the directory is not empty" in
        new FileWrapperLike_NotEmptyDirectoryFixture {
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [IOException])
        }
    }
  }

  protected def wrap(file: F): W
  protected def fileNames(files: Seq[F]): Seq[String] = files.map(wrap).map(_.fileName)
  protected def fileNameContains(s: String): F => Boolean = wrap(_).fileName.contains(s)

  "***** File Operations through Files and/or Directory Structure *****" - {

    "***** eachFile[Match][Recurse]() *****" - {

      "eachFile{} method should" - {

        "iterate files and directories in the specified directory (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFile(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt", "dir1", "dir2", "dir3")
          }
      }

      "eachFile(FileType){} method should" - {

        "iterate files in the specified directory if the argument is FileType.Files (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFile(FileType.Files)(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt")
          }

        "iterate directories in the specified directory if the argument is FileType.Directories (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFile(FileType.Directories)(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("dir1", "dir2", "dir3")
          }

        "iterate files and directories in the specified directory if the argument is FileType.Any (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFile(FileType.Any)(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt", "dir1", "dir2", "dir3")
          }
      }

      "eachFileMatch(F =>Boolean){} method should" - {

        "iterate files and directories which matches the specified condition in the specified directory (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatch(fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child2.txt", "dir2")
          }
      }

      "eachFileMatch(FileType, F => Boolean){} method should" - {

        "iterate files which matches the specified condition in the specified directory if the FileType argument is Files (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatch(FileType.Files, fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("child2.txt")
          }

        "iterate directories which matches the specified condition in the specified directory if the FileType argument is Directories (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatch(FileType.Directories, fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("dir2")
          }

        "iterate files and directories which matches the specified condition in the specified directory if the FileType argument is Any (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatch(FileType.Any, fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child2.txt", "dir2")
          }
      }

      "eachFileRecurse(FileType, Boolean){} method should" - {

        "deeply iterate files in the specified directory if the FileType argument is Files" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse(FileType.Files)(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt",
                "child11.txt", "child12.txt",
                "child21.txt", "child22.txt", "child23.txt",
                "child31.txt", "child311.txt", "child312.txt")
          }

        "deeply iterate directories in the specified directory if the FileType argument is Directories" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse(FileType.Directories)(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq(sut.fileName, "dir1", "dir2", "dir3", "dir31")
          }

        "deeply iterate files and directories in the specified directory if the FileType argument is FileType.Any" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse(FileType.Any)(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt",
                "child11.txt", "child12.txt",
                "child21.txt", "child22.txt", "child23.txt",
                "child31.txt", "child311.txt", "child312.txt",
                sut.fileName, "dir1", "dir2", "dir3", "dir31")
          }

        "deeply iterate files and directories in the specified directory if the FileType argument is omitted" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse()(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt",
                "child11.txt", "child12.txt",
                "child21.txt", "child22.txt", "child23.txt",
                "child31.txt", "child311.txt", "child312.txt",
                sut.fileName, "dir1", "dir2", "dir3", "dir31")
          }

        "visit each directory after its children if visitDirectoryPost is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val files = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse(FileType.Any, visitDirectoryPost = true)(files += _)
            __Verify__
            val result = fileNames(files)
            result should contain inOrder("child11.txt", "dir1", sut.fileName)
            result should contain inOrder("child12.txt", "dir1", sut.fileName)

            result should contain inOrder("child21.txt", "dir2", sut.fileName)
            result should contain inOrder("child22.txt", "dir2", sut.fileName)
            result should contain inOrder("child23.txt", "dir2", sut.fileName)

            result should contain inOrder("child311.txt", "dir31", "dir3", sut.fileName)
            result should contain inOrder("child312.txt", "dir31", "dir3", sut.fileName)
          }

        "visit each directory before its children if visitDirectoryPost is false (DO deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val files = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse(FileType.Any, visitDirectoryPost = false)(files += _)
            __Verify__
            val result = fileNames(files)
            result should contain inOrder(sut.fileName, "dir1", "child11.txt")
            result should contain inOrder(sut.fileName, "dir1", "child12.txt")

            result should contain inOrder(sut.fileName, "dir2", "child21.txt")
            result should contain inOrder(sut.fileName, "dir2", "child22.txt")
            result should contain inOrder(sut.fileName, "dir2", "child23.txt")

            result should contain inOrder(sut.fileName, "dir3", "dir31", "child311.txt")
            result should contain inOrder(sut.fileName, "dir3", "dir31", "child312.txt")
          }

        "visit each directory before its children if visitDirectoryPost is omitted" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val files = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse(FileType.Any)(files += _)
            __Verify__
            val result = fileNames(files)
            result should contain inOrder(sut.fileName, "dir1", "child11.txt")
            result should contain inOrder(sut.fileName, "dir1", "child12.txt")

            result should contain inOrder(sut.fileName, "dir2", "child21.txt")
            result should contain inOrder(sut.fileName, "dir2", "child22.txt")
            result should contain inOrder(sut.fileName, "dir2", "child23.txt")

            result should contain inOrder(sut.fileName, "dir3", "dir31", "child311.txt")
            result should contain inOrder(sut.fileName, "dir3", "dir31", "child312.txt")
          }
      }


      "eachFileMatchRecurse(F => Boolean, FileType, Boolean){} method should" - {

        "deeply iterate files which matches the specified condition in the specified directory if the FileType argument is Files" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Files, fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child2.txt", "child12.txt", "child21.txt", "child22.txt", "child23.txt", "child312.txt")
          }

        "deeply iterate directories which matches the specified condition in the specified directory if the FileType argument is Directories" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Directories, fileNameContains("3"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq(sut.fileName, "dir3", "dir31")
          }

        "iterate files and directories which matches the specified condition in the specified directory if the FileType argument is Any" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Any, fileNameContains("3"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child3.txt", "child23.txt",
                "child31.txt", "child311.txt", "child312.txt",
                sut.fileName, "dir3", "dir31")
          }

        "visit each directory after its children if visitDirectoryPost is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Any, fileNameContains("3"), visitDirectoryPost = true)(result += _)
            __Verify__
            fileNames(result) should contain inOrder("child312.txt", "dir31", "dir3", sut.fileName)
          }

        "visit each directory before its children if visitDirectoryPost is false (DO deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Any, fileNameContains("3"), visitDirectoryPost = false)(result += _)
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31", "child311.txt")
          }

        "visit each directory before its children if visitDirectoryPost is omitted" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Any, fileNameContains("3"))(result += _)
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31", "child312.txt")
          }
      }
    }

    "***** eachDir[Match][Recurse] *****" - {

      "eachDir{} method should" - {

        "iterate directories in the specified directory (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachDir(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("dir1", "dir2", "dir3")
          }
      }

      "eachDirMatch(F => Boolean){} method should" - {

        "iterate directories which matches the specified condition in the specified directory (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirMatch(fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("dir2")
          }
      }

      "eachDirRecurse{} method should" - {

        "deeply iterate directories in the specified directory" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirRecurse(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq(sut.fileName, "dir1", "dir2", "dir3", "dir31")
          }

        "visit each directory before its children." in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val dirs = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirRecurse(dirs += _)
            __Verify__
            val result = fileNames(dirs)
            result should contain inOrder (sut.fileName, "dir1")
            result should contain inOrder (sut.fileName, "dir2")
            result should contain inOrder (sut.fileName, "dir3", "dir31")
          }
      }

      "eachDirRecurse(Boolean){} method should" - {

        "visit each directory after its children if visitParentPost is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val dirs = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirRecurse(visitParentPost = true)(dirs += _)
            __Verify__
            val result = fileNames(dirs)
            result should contain inOrder("dir1", sut.fileName)
            result should contain inOrder("dir2", sut.fileName)
            result should contain inOrder("dir31", "dir3", sut.fileName)
          }

        "visit each directory before its children if visitDirectoryPost is false" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val files = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirRecurse(visitParentPost = false)(files += _)
            __Verify__
            val result = fileNames(files)
            result should contain inOrder(sut.fileName, "dir1")
            result should contain inOrder(sut.fileName, "dir2")
            result should contain inOrder(sut.fileName, "dir3", "dir31")
          }
      }


      "eachDirMatchRecurse(F => Boolean, Boolean){} method should" - {

        "deeply iterate directories which matches the specified condition in the specified directory" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirMatchRecurse(fileNameContains("3"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq(sut.fileName, "dir3", "dir31")
          }

        "visit directory after its children if visitParentPost is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirMatchRecurse(fileNameContains("3"), visitParentPost = true)(result += _)
            __Verify__
            fileNames(result) should contain inOrder("dir31", "dir3", sut.fileName)
          }

        "visit directory before its children if visitParentPost is false" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val files = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirMatchRecurse(fileNameContains("3"), visitParentPost = false)(files += _)
            __Verify__
            fileNames(files) should contain inOrder(sut.fileName, "dir3", "dir31")
          }

        "visit directory before its children if visitDirectoryPost is omitted" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val files = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirMatchRecurse(fileNameContains("3"))(files += _)
            __Verify__
            fileNames(files) should contain inOrder(sut.fileName, "dir3", "dir31")
          }
      }
    }
  }

  "***** Directory Operations *****" - {

    "deleteDir() method should" - {

      "delete the directory even if not empty" in new FileWrapperLike_DirectoryFixture {
        __Exercise__
        val result = sut.deleteDir()
        __Verify__
        dir should not (exist)
        result should be (None)
      }
    }
  }
}

class FileWrapperSpec extends FileWrapperLikeSpec[File, FileWrapper] with GluinoFile{

  override protected def asF(path: Path) = path.toFile
  override protected def newFileWrapperLike(path: Path) = new FileWrapper(path.toFile)
  override protected def wrap(file: File): FileWrapper = new FileWrapper(file)

  trait FileOperationFixture{
    val file = new File("path/to/some/dir")
  }

  "/" - {
    "resolve a child path with the specified String" in new FileOperationFixture {
      __Exercise__
      val child = file / "child.txt"
      __Verify__
      child should equal(new File("path/to/some/dir/child.txt"))
    }
  }

  "\\" - {
    "resolve a child path with the specified String" in new FileOperationFixture {
      __Exercise__
      val child = file \ "child.txt"
      __Verify__
      child should equal(new File("path/to/some/dir\\child.txt"))
    }
  }
}
