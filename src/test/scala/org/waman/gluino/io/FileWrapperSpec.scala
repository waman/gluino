package org.waman.gluino.io

import java.io.{File, IOException}
import java.nio.file.{Files, Path}

import org.scalatest.BeforeAndAfterAll
import org.scalatest.OptionValues._
import org.waman.gluino.nio.{DirectoryBuilder, GluinoPath, PathWrapper}
import org.waman.gluino.number.GluinoNumber

import scala.collection.mutable

trait FileWrapperLikeSpec[F, W <: FileWrapperLike[F, W]]
    extends InputStreamWrapperLikeSpec
    with OutputStreamWrapperLikeSpec[W]
    with GluinoNumber with BeforeAndAfterAll{

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
  override def afterAll{ println(new PathWrapper(readOnlyDir).deleteDir()) }

  trait FileWrapperLike_ReadOnlyDirFixture{
    val sut = newFileWrapperLike(readOnlyDir)
  }

  private def initDirectory(parent: Path = GluinoPath.createTempDirectory(prefix = "123-")): Path = {
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
          file("child312.txt")
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
  def fileNames(files: Seq[F]): Seq[String] = files.map(wrap).map(_.fileName)
  def fileNameContains(s: String): F => Boolean = wrap(_).fileName.contains(s)

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
