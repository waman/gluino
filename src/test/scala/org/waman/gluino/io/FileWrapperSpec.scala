package org.waman.gluino.io

import java.io.{File, IOException}
import java.nio.file.{Files, Path}

import scala.collection.mutable

import org.scalatest.OptionValues._
import org.waman.gluino.nio.{DirectoryBuilder, GluinoPath}
import org.waman.gluino.number.GluinoNumber

trait FileWrapperLikeSpec[F, W <: FileWrapperLike[F, W]]
    extends InputStreamWrapperLikeSpec
    with OutputStreamWrapperLikeSpec[W]
    with GluinoNumber{

  protected def newFileWrapperLike(path: Path): W

  override protected def newInputStreamWrapperLike(path: Path) = newFileWrapperLike(path)
  override protected def newOutputStreamWrapperLike(path: Path) = newFileWrapperLike(path)

  trait FileWrapperLike_FileFixture extends FileFixture{
    val sut = newFileWrapperLike(path)
  }

  trait DirectoryFixture{
    val dir = GluinoPath.createTempDirectory()
  }

  trait FileWrapperLike_DirectoryFixture extends DirectoryFixture{
    val sut = newFileWrapperLike(dir)
  }

  trait DirectoryWithAFileFixture extends DirectoryFixture{
    val childPath = GluinoPath.createTempFile(dir)
  }

  trait FileWrapperLike_DirectoryWithAFileFixture extends DirectoryWithAFileFixture{
    val sut = newFileWrapperLike(dir)
  }

  val readOnlyDir: Path = initDirectory()

  trait FileWrapperLike_ReadOnlyDirFixture{
    val sut = newFileWrapperLike(readOnlyDir)
  }

  private def initDirectory(parent: Path = GluinoPath.createTempDirectory()): Path = {
    new DirectoryBuilder{
      val baseDir = parent
      file("child1.txt")
      file("child2.txt")
      file("child3.txt")
      dir("dir1"){
        file("child11.txt")
        file("child12.txt")
      }
      dir("dir2"){
        file("child21.txt")
        file("child22.txt")
        file("child23.txt")
      }
      dir("dir3"){
        file("child31.txt")
        dir("dir31"){
          file("child311.txt")
          file("child322.txt")
        }
      }
    }.baseDir
  }

  trait DirectoryWithFilesFixture extends DirectoryFixture{
    initDirectory(dir)
  }

  trait FileWrapperLike_DirectoryWithFilesFixture extends DirectoryWithFilesFixture{
    val sut = newFileWrapperLike(dir)
  }

  "***** File Operations *****" - {

    "delete() method should" - {

      "delete the file" in new FileWrapperLike_FileFixture{
        __Exercise__
        sut.delete()
        __Verify__
        path should not (exist)
      }

      "delete the directory" in new FileWrapperLike_DirectoryFixture {
        __Exercise__
        sut.delete()
        __Verify__
        dir should not (exist)
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
        new FileWrapperLike_DirectoryWithAFileFixture {
          __Exercise__
          val result = sut.delete()
          __Verify__
          result.value should be (a [IOException])
        }
    }
  }

  def wrap(file: F): W

  "***** File Operations through Files and/or Directory Structure *****" - {

    "eachFile() method should" - {

      "iterate files and directories in the specified directory (NOT deeply iterate)" in
        new FileWrapperLike_ReadOnlyDirFixture {
          __SetUp__
          val result = new mutable.MutableList[F]
          __Exercise__
          sut.eachFile(result += _)
          __Verify__
          result.map(wrap).map(_.fileName) should contain theSameElementsAs
            Set("child1.txt", "child2.txt", "child3.txt", "dir1", "dir2", "dir3")
        }
    }

    "eachFile(FileType) method should" - {

      "iterate files in the specified directory if FileType.Files is passed (NOT deeply iterate)" in
        new FileWrapperLike_ReadOnlyDirFixture {
          __SetUp__
          val result = new mutable.MutableList[F]
          __Exercise__
          sut.eachFile(FileType.Files)(result += _)
          __Verify__
          result.map(wrap).map(_.fileName) should contain theSameElementsAs
            Set("child1.txt", "child2.txt", "child3.txt")
        }

      "iterate directories in the specified directory if FileType.Directories is passed (NOT deeply iterate)" in
        new FileWrapperLike_ReadOnlyDirFixture {
          __SetUp__
          val result = new mutable.MutableList[F]
          __Exercise__
          sut.eachFile(FileType.Directories)(result += _)
          __Verify__
          result.map(wrap).map(_.fileName) should contain theSameElementsAs
            Set("dir1", "dir2", "dir3")
        }

      "iterate files and directories in the specified directory if FileType.Any is passed (NOT deeply iterate)" in
        new FileWrapperLike_ReadOnlyDirFixture {
          __SetUp__
          val result = new mutable.MutableList[F]
          __Exercise__
          sut.eachFile(FileType.Any)(result += _)
          __Verify__
          result.map(wrap).map(_.fileName) should contain theSameElementsAs
            Set("child1.txt", "child2.txt", "child3.txt", "dir1", "dir2", "dir3")
        }
    }

    "eachFileMatch(F => Boolean) method should" - {

      "iterate files and directories which matches the specified condition in the specified directory (NOT deeply iterate)" in
        new FileWrapperLike_ReadOnlyDirFixture {
          __SetUp__
          val result = new mutable.MutableList[F]
          __Exercise__
          sut.eachFileMatch(wrap(_).fileName.contains("2"))(result += _)
          __Verify__
          result.map(wrap).map(_.fileName) should contain theSameElementsAs
            Set("child2.txt", "dir2")
        }
    }

    "eachDir() method should" - {

      "iterate directories in the specified directory (NOT deeply iterate)" in
        new FileWrapperLike_ReadOnlyDirFixture {
          __SetUp__
          val result = new mutable.MutableList[F]
          __Exercise__
          sut.eachDir(result += _)
          __Verify__
          result.map(wrap).map(_.fileName) should contain theSameElementsAs
            Set("dir1", "dir2", "dir3")
        }
    }

    "eachDirMatch(F => Boolean) method should" - {

      "iterate directories which matches the specified condition in the specified directory (NOT deeply iterate)" in
        new FileWrapperLike_ReadOnlyDirFixture {
          __SetUp__
          val result = new mutable.MutableList[F]
          __Exercise__
          sut.eachDirMatch(wrap(_).fileName.contains("2"))(result += _)
          __Verify__
          result.map(wrap).map(_.fileName) should contain theSameElementsAs
            Set("dir2")
        }
    }
  }
}

class FileWrapperSpec extends FileWrapperLikeSpec[File, FileWrapper] with GluinoFile{

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

  override protected def newFileWrapperLike(path: Path) = new FileWrapper(path.toFile)
  override def wrap(file: File): FileWrapper = new FileWrapper(file)
}
