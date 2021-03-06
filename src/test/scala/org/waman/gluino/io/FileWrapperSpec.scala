package org.waman.gluino.io

import java.io.{File, IOException}
import java.nio.file.{FileVisitResult, Files, Path}

import org.scalatest.OptionValues._
import org.waman.gluino.nio.{DirectoryBuilder, GluinoPath}
import org.waman.gluino.number.GluinoNumber
import org.waman.scalatest_util.WindowsAdministrated

import scala.collection.JavaConversions._
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

  trait FileWrapperLike_NotExistingFileFixture extends NotExistingFileFixture{
    val sut = newFileWrapperLike(path)
  }

  // Directory Fixture
  trait FileWrapperLike_DirectoryFixture extends DirectoryFixture{
    val sut = newFileWrapperLike(dir)
  }

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

  trait FileWrapperLike_NotExistingDirectoryFixture extends NotExistingDirectoryFixture{
    val sut = newFileWrapperLike(dir)
  }

  // Link and Symbolic Link Fixture
  trait FileWrapperLike_LinkFixture extends LinkFixture{
    val sut = newFileWrapperLike(link)
  }

  trait FileWrapperLike_SymbolicLinkFixture extends SymbolicLinkFixture{
    val sut = newFileWrapperLike(symbolicLink)
  }

  "Size of file and directory" - {

    "size method should return file size" in {
      __SetUp__
      val sut = newFileWrapperLike(readOnlyBigPath)
      __Verify__
      sut.size should equal (26000L)
    }

    "directorySize method should return sum of file sizes in the directory (deeply iterate)" in {
      __SetUp__
      import org.waman.gluino.nio.GluinoPath._
      val dir = new DirectoryBuilder{
        val baseDir = createTempDirectory(deleteOnExit = true)

        file("child1.txt") << readOnlyBigPath
        file("child2.txt") << readOnlyBigPath
        file("child3.txt") << readOnlyBigPath
        dir("dir1") {
          file("child11.txt") << readOnlyBigPath
          file("child12.txt") << readOnlyBigPath
        }
        dir("dir2") {
          file("child21.txt") << readOnlyBigPath
          file("child22.txt") << readOnlyBigPath
          file("child23.txt") << readOnlyBigPath
        }
        dir("dir3") {
          file("child31.txt") << readOnlyBigPath
          dir("dir31") {
            file("child311.txt") << readOnlyBigPath
            file("child312.txt") << readOnlyBigPath
          }
        }
      }.baseDir
      val sut = newFileWrapperLike(dir)
      __Verify__
      sut.directorySize should equal ((26000L * 11) +- 100L)
    }
  }

  "isOlder/NewerThan() methods" - {

    "isOlderThan() method should" - {

      "return true if this file is older than the arg file" in new FileFixture {
        __SetUp__
        val sut = newFileWrapperLike(path)
        Thread.sleep(10)
        val arg = asF(GluinoPath.createTempFile(deleteOnExit = true))
        __Exercise__
        val result = sut isOlderThan arg
        __Verify__
        result should be (true)
      }

      "return false if this file is created at the same time as the arg file" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val arg = asF(path)
          __Exercise__
          val result = sut isOlderThan arg
          __Verify__
          result should be (false)
        }

      "return false if this file is newer than the arg file" in new FileFixture {
        __SetUp__
        val arg = asF(path)
        Thread.sleep(10)
        val sut = newFileWrapperLike(GluinoPath.createTempFile(deleteOnExit = true))
        __Exercise__
        val result = sut isOlderThan arg
        __Verify__
        result should be (false)
      }
    }

    "isNewerThan() method should" - {

      "return false if this file is older than the arg file" in new FileFixture {
        __SetUp__
        val sut = newFileWrapperLike(path)
        Thread.sleep(10)
        val arg = asF(GluinoPath.createTempFile(deleteOnExit = true))
        __Exercise__
        val result = sut isNewerThan arg
        __Verify__
        result should be (false)
      }

      "return false if this file is created at the same time as the arg file" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val arg = asF(path)
          __Exercise__
          val result = sut isNewerThan arg
          __Verify__
          result should be (false)
        }

      "return true if this file is newer than the arg file" in new FileFixture {
        __SetUp__
        val arg = asF(path)
        Thread.sleep(10)
        val sut = newFileWrapperLike(GluinoPath.createTempFile(deleteOnExit = true))
        __Exercise__
        val result = sut isNewerThan arg
        __Verify__
        result should be (true)
      }
    }
  }

  "***** set content in a lump *****" - {

    "Bytes" - {

      "bytes_=() method should" - {

        "write bytes to this file" in new FileWrapperLike_FileWithContentFixture {
          __Exercise__
          sut.bytes = "内容".getBytes(ISO2022)
          __Verify__
          text(path, ISO2022) should equal ("内容")
        }
      }
    }

    "Text" - {

      "setText() method should" - {

        "set the content of this file to the specified text" in
          new FileWrapperLike_FileWithContentFixture {
            __Exercise__
            sut.setText("Some content")
            __Verify__
            text(path) should equal ("Some content")
          }

        "set the content of this file to the specified text with the specified encoding" in
          new FileWrapperLike_FileWithContentFixture {
            __Exercise__
            sut.setText("内容", ISO2022)
            __Verify__
            text(path, ISO2022) should equal ("内容")
          }
      }

      "text_=() method should" - {

        "set the content of this file to the specified text" in
          new FileWrapperLike_FileWithContentFixture {
            __Exercise__
            sut.text = "Some content"
            __Verify__
            text(path) should equal ("Some content")
          }
      }
    }
  }

  "***** Tests of fileIO for NOT existing file *****" - {

    "Byte array" - {

      "Reading 'bytes' property should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.bytes }
        }

      "Writing 'bytes' property should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.bytes = "Some content".getBytes
          __Verify__
          path should exist
          text(path) should equal ("Some content")
        }
    }

    "Byte stream (InputStream/OutputStream)" - {

      // newXxxxInputStream()
      "newInputStream() method should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.newInputStream() }
        }

      "newDataInputStream() method should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.newDataInputStream() }
        }

      "newObjectInputStream() method should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.newObjectInputStream() }
        }

      "newObjectInputStream(ClassLoader) method should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy {
            sut.newObjectInputStream(ClassLoader.getSystemClassLoader)
          }
        }

      // withXxxxInputStream()
      "withInputStream() method should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.withInputStream(f => f) }
        }

      "withDataInputStream() method should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.withDataInputStream(f => f) }
        }

      "withObjectInputStream() method should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.withObjectInputStream(f => f) }
        }

      "withObjectInputStream(ClassLoader)() method should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy {
            sut.withObjectInputStream(ClassLoader.getSystemClassLoader)(f => f)
          }
        }

      // newXxxxOutputStream()
      "newOutputStream() method should create a new file" - {

        "the 'append' arg is omitted" in new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.newOutputStream().close()
          __Verify__
          path should exist
        }

        "the 'append' arg is true" in new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.newOutputStream(append = true).close()
          __Verify__
          path should exist
        }

        "the 'append' arg is false" in new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.newOutputStream(append = false).close()
          __Verify__
          path should exist
        }
      }

      "newDataOutputStream() method should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.newDataOutputStream().close()
          __Verify__
          path should exist
        }

      "newObjectOutputStream() method should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.newObjectOutputStream().close()
          __Verify__
          path should exist
        }

      // withXxxxOutputStream()
      "withOutputStream() method should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.withOutputStream(f => f)
          __Verify__
          path should exist
        }

      "withOutputStreamAppend() method should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.withOutputStreamAppend(f => f)
          __Verify__
          path should exist
        }

      "withDataOutputStream() method should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.withDataOutputStream(f => f)
          __Verify__
          path should exist
        }

      "withObjectOutputStream() method should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.withObjectOutputStream(f => f)
          __Verify__
          path should exist
        }
    }

    "String" - {

      "Reading 'text' property should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.text }
        }

      "text(Charset) should throw IOException" in
        new FileWrapperLike_NotExistingFileFixture {
          __Verify__
          an [IOException] should be thrownBy { sut.text(ISO2022) }
        }

      "Writing 'text' property should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.text = "Some content"
          __Verify__
          path should exist
          text(path) should equal("Some content")
        }

      "setText(String) should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.setText("Some content")
          __Verify__
          path should exist
          text(path) should equal("Some content")
        }

      "setText(String, Charset) should create a new file" in
        new FileWrapperLike_NotExistingFileFixture {
          __Exercise__
          sut.setText("内容", ISO2022)
          __Verify__
          path should exist
          text(path, ISO2022) should equal("内容")
        }
    }

    "Character stream (Reader/Writer)" - {

      // newReader/Writer()
      "newReader() method should" - {

        "throw IOException (with no arg)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Verify__
            an[IOException] should be thrownBy {
              sut.newReader()
            }
          }

        "throw IOException (character set is specified)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Verify__
            an[IOException] should be thrownBy {
              sut.newReader(ISO2022)
            }
          }
      }

      "newWriter() method should" - {

        "crate this file (with no arg)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.newWriter().close()
            __Verify__
            path should exist
          }

        "crate this file (character set is specified)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.newWriter(ISO2022).close()
            __Verify__
            path should exist
          }

        "crate this file (with append arg)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.newWriter(append = true).close()
            __Verify__
            path should exist
          }

        "crate this file (with charset and append)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.newWriter(ISO2022, append = true).close()
            __Verify__
            path should exist
          }
      }

      "newPrintWriter() method should" - {

        "crate this file (with no arg)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.newPrintWriter().close()
            __Verify__
            path should exist
          }

        "crate this file (character set is specified)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.newPrintWriter(ISO2022).close()
            __Verify__
            path should exist
          }

        "crate this file (with append arg)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.newPrintWriter(append = true).close()
            __Verify__
            path should exist
          }

        "crate this file (with charset and append)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.newPrintWriter(ISO2022, append = true).close()
            __Verify__
            path should exist
          }
      }

      // withReader/Writer()
      "withReader() method should" - {

        "throw IOException" in
          new FileWrapperLike_NotExistingFileFixture {
            __Verify__
            an [IOException] should be thrownBy { sut.withReader(f => f) }
          }

        "throw IOException (charset is specified)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Verify__
            an [IOException] should be thrownBy { sut.withReader(ISO2022)(f => f) }
          }
      }

      "withWriter() method should" - {

        "crate this file" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.withWriter(f => f)
            __Verify__
            path should exist
          }

        "create this file (charset is specified)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.withWriter(ISO2022)(f => f)
            __Verify__
            path should exist
          }
      }

      "withWriterAppend() method should" - {

        "crate this file" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.withWriterAppend(f => f)
            __Verify__
            path should exist
          }

        "create this file (charset is specified)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.withWriterAppend(ISO2022)(f => f)
            __Verify__
            path should exist
          }
      }

      "withPrintWriter() method should" - {

        "crate this file" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.withPrintWriter(f => f)
            __Verify__
            path should exist
          }

        "create this file (charset is specified)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.withPrintWriter(ISO2022)(f => f)
            __Verify__
            path should exist
          }
      }

      "withPrintWriterAppend() method should" - {

        "crate this file" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.withPrintWriterAppend(f => f)
            __Verify__
            path should exist
          }

        "create this file (charset is specified)" in
          new FileWrapperLike_NotExistingFileFixture {
            __Exercise__
            sut.withPrintWriterAppend(ISO2022)(f => f)
            __Verify__
            path should exist
          }
      }
    }
  }

  "***** File Operations *****" - {

    "createFile() method should" - {

      "create a file" in new FileWrapperLike_NotExistingFileFixture {
        __Exercise__
        val result = sut.createFile()
        __Verify__
        result should be (None)
        path should exist
        sut should be a 'file
      }

      "RETURN an Option[IOException] if the file already exist" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.createFile()
          __Verify__
          result.value should be (a [IOException])
        }
    }

    "createDirectory() method should" - {

      "create a directory" in new FileWrapperLike_NotExistingDirectoryFixture {
        __Exercise__
        val result = sut.createDirectory()
        __Verify__
        result should be (None)
        dir should exist
        sut should be a 'directory
      }

      "RETURN an Option[IOException] if the same-name file already exists" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.createDirectory()
          __Verify__
          result.value should be (a [IOException])
        }

      "do nothing if the directory already exists" in
        new FileWrapperLike_DirectoryFixture {
          __Exercise__
          val result = sut.createDirectory()
          __Verify__
          result.value should be (a [IOException])
        }
    }

    "createDirectories() method should" - {

      "create ancestor directories" in new NotExistingDirectoryFixture {
        __SetUp__
        val dddir = dir.resolve("ddir").resolve("dddir")
        val sut = newFileWrapperLike(dddir)
        __Exercise__
        val result = sut.createDirectories()
        __Verify__
        result should be (None)
        dddir should exist
        sut should be a 'directory
      }

      "RETURN an Option[IOException] if the same-name file already exists" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.createDirectories()
          __Verify__
          result.value should be (a [IOException])
        }

      "do nothing if the directory already exists" in
        new FileWrapperLike_DirectoryFixture {
          __Exercise__
          val result = sut.createDirectories()
          __Verify__
          result should be (None)
        }
    }

    "renameTo() method should" - {

      "(for Files)" - {

        "rename a file" in new FileWrapperLike_FileWithContentFixture {
          __SetUp__
          val targetPath = createNotExistingFile()
          val target = asF(targetPath)
          __Exercise__
          val result = sut.renameTo(target)
          __Verify__
          result should be (None)
          path should not (exist)
          targetPath should exist
          text(targetPath) should equal(contentAsString)
        }

        "RETURN an Option[IOException] if the target file exists and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempFile(deleteOnExit = true)
            val target = asF(targetPath)
            __Exercise__
            val result = sut.renameTo(target)
            __Verify__
            result.value should be(a[IOException])
            path should exist
            targetPath should exist
          }

        "RETURN an Option[IOException] if the target file exists and the 'isOverride' arg is false" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempFile(deleteOnExit = true)
            val target = asF(targetPath)
            __Exercise__
            val result = sut.renameTo(target, isOverride = false)
            __Verify__
            result.value should be(a[IOException])
            path should exist
            targetPath should exist
          }

        "rename a file even if the target file exists when the 'isOverride' arg is true" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempFile(deleteOnExit = true)
            Files.write(targetPath, Seq("Some content."))
            val target = asF(targetPath)
            __Exercise__
            sut.renameTo(target, isOverride = true)
            __Verify__
            path should not (exist)
            targetPath should exist
            text(targetPath) should equal(contentAsString)
          }

        "do nothing if the source and target are the same file and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val target = asF(path)
            __Exercise__
            val result = sut.renameTo(target)
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
            val result = sut.renameTo(target, isOverride = false)
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
            val result = sut.renameTo(target, isOverride = true)
            __Verify__
            result should be (None)
            path should exist
            text(path) should equal(contentAsString)
          }

        "rename a file by renameTo(String) method" in new FileWrapperLike_FileWithContentFixture {
          __SetUp__
          val targetPath = createNotExistingFile()
          val target = targetPath.toString
          __Exercise__
          val result = sut.renameTo(target)
          __Verify__
          result should be (None)
          path should not (exist)
          targetPath should exist
          text(targetPath) should equal(contentAsString)
        }

        "rename a file by renameTo(String, Boolean) method" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
            val target = targetPath.toString
            __Exercise__
            val result = sut.renameTo(target, isOverride = true)
            __Verify__
            result should be (None)
            path should not (exist)
            targetPath should exist
            text(targetPath) should equal(contentAsString)
          }
      }

      "(for Directories)" - {

        "rename the directory" in new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val targetPath = createNotExistingDirectory()
          val target = asF(targetPath)
          __Exercise__
          val result = sut.renameTo(target)
          __Verify__
          result should be (None)
          dir should not (exist)
          targetPath should exist
        }

        "RETURN an Option[IOException] if the target directory exists and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
            val target = asF(targetPath)
            __Exercise__
            val result = sut.renameTo(target)
            __Verify__
            result.value should be (a [IOException])
            path should exist
            targetPath should exist
          }

        "RETURN an Option[IOException] if the target directory exists and the 'isOverride' arg is false" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
            val target = asF(targetPath)
            __Exercise__
            val result = sut.renameTo(target, isOverride = false)
            __Verify__
            result.value should be (a [IOException])
            path should exist
            targetPath should exist
          }

        "rename the directory even if the target directory exists when the 'isOverride' arg is true" in
          new FileWrapperLike_DirectoryFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
            val target = asF(targetPath)
            __Exercise__
            val result = sut.renameTo(target, isOverride = true)
            __Verify__
            result should be (None)
            dir should not (exist)
            targetPath should exist
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is omitted" in
          new FileWrapperLike_NotEmptyDirectoryFixture{
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.renameTo(target)
              __Verify__
              result.value should be (a [IOException])
              dir should exist
              targetPath should exist
            }
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is false" in
          new FileWrapperLike_NotEmptyDirectoryFixture {
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.renameTo(target, isOverride = false)
              __Verify__
              result.value should be (a [IOException])
              dir should exist
              targetPath should exist
            }
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is true" in
          new FileWrapperLike_NotEmptyDirectoryFixture {
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.renameTo(target, isOverride = true)
              __Verify__
              result.value should be (a [IOException])
              dir should exist
            }
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is omitted" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.renameTo(target)
            __Verify__
            result should be (None)
            dir should exist
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is false" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.renameTo(target, isOverride = false)
            __Verify__
            result should be (None)
            dir should exist
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is true" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.renameTo(target, isOverride = true)
            __Verify__
            result should be (None)
            dir should exist
          }
      }

      "(for Links)" - {

        "rename a link" in new FileWrapperLike_LinkFixture {
          __SetUp__
          val targetPath = createNotExistingFile(suffix = ".lnk")
          val target = asF(targetPath)
          __Exercise__
          val result = sut.renameTo(target)
          __Verify__
          result should be (None)
          link should not (exist)
          targetPath should exist
          text(targetPath) should equal (contentAsString)
          linkTarget should exist
          text(linkTarget) should equal (contentAsString)
        }
      }

      "(for Symbolic Links)" - {

        "rename a symbolic link" taggedAs WindowsAdministrated in
          new WindowsAdministratorRequirement {
            new FileWrapperLike_SymbolicLinkFixture {
              __SetUp__
              val targetPath = createNotExistingFile(suffix = ".symlink")
              val target = asF(targetPath)
              __Exercise__
              val result = sut.renameTo(target)
              __Verify__
              result should be(None)
              symbolicLink should not(exist)
              targetPath should exist
              text(targetPath) should equal(contentAsString)
              linkTarget should exist
              text(linkTarget) should equal(contentAsString)
            }
          }
      }
    }

    "move() method should" - {

      "(for Files)" - {

        "move a file" in new FileWrapperLike_FileWithContentFixture {
          __SetUp__
          val targetPath = createNotExistingFile()
          val target = asF(targetPath)
          __Exercise__
          val result = sut.move(target)
          __Verify__
          result should be (None)
          path should not (exist)
          targetPath should exist
          text(targetPath) should equal(contentAsString)
        }

        "RETURN an Option[IOException] if the target file exists and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempFile(deleteOnExit = true))
            __Exercise__
            val result = sut.move(target)
            __Verify__
            result.value should be(a[IOException])
            path should exist
          }

        "RETURN an Option[IOException] if the target file exists and the 'isOverride' arg is false" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempFile(deleteOnExit = true))
            __Exercise__
            val result = sut.move(target, isOverride = false)
            __Verify__
            result.value should be(a[IOException])
            path should exist
          }

        "move a file even if the target file exists when the 'isOverride' arg is true" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempFile(deleteOnExit = true)
            Files.write(targetPath, Seq("Some content."))
            val target = asF(targetPath)
            __Exercise__
            val result = sut.move(target, isOverride = true)
            __Verify__
            result should be (None)
            path should not (exist)
            targetPath should exist
            text(targetPath) should equal(contentAsString)
          }

        "do nothing if the source and target are the same file and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileWithContentFixture {
            __SetUp__
            val target = asF(path)
            __Exercise__
            val result = sut.move(target)
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
            val result = sut.move(target, isOverride = false)
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
            val result = sut.move(target, isOverride = true)
            __Verify__
            result should be (None)
            path should exist
            text(path) should equal(contentAsString)
          }
      }

      "(for Directories)" - {

        "move the directory" in new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val targetPath = createNotExistingDirectory()
          val target = asF(targetPath)
          __Exercise__
          val result = sut.move(target)
          __Verify__
          result should be (None)
          dir should not (exist)
          targetPath should exist
        }

        "RETURN an Option[IOException] if the target directory exists and the 'isOverride' arg is omitted" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempDirectory(deleteOnExit = true))
            __Exercise__
            val result = sut.move(target)
            __Verify__
            result.value should be (a [IOException])
            path should exist
          }

        "RETURN an Option[IOException] if the target directory exists and the 'isOverride' arg is false" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempDirectory(deleteOnExit = true))
            __Exercise__
            val result = sut.move(target, isOverride = false)
            __Verify__
            result.value should be (a [IOException])
            path should exist
          }

        "move the directory even if the target directory exists when the 'isOverride' arg is true" in
          new FileWrapperLike_DirectoryFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
            val target = asF(targetPath)
            __Exercise__
            val result = sut.move(target, isOverride = true)
            __Verify__
            result should be (None)
            dir should not (exist)
            targetPath should exist
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is omitted" in
          new FileWrapperLike_NotEmptyDirectoryFixture{
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.move(target)
              __Verify__
              result.value should be (a [IOException])
              dir should exist
            }
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is false" in
          new FileWrapperLike_NotEmptyDirectoryFixture {
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.move(target, isOverride = false)
              __Verify__
              result.value should be (a [IOException])
              dir should exist
            }
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is omitted" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.move(target)
            __Verify__
            result should be (None)
            dir should exist
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is false" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.move(target, isOverride = false)
            __Verify__
            result should be (None)
            dir should exist
          }

        "do nothing if the source and target are the same directory and the 'isOverride' arg is true" in
          new FileWrapperLike_DirectoryFixture{
            __SetUp__
            val target = asF(dir)
            __Exercise__
            val result = sut.move(target, isOverride = true)
            __Verify__
            result should be (None)
            dir should exist
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is true" in
          new FileWrapperLike_NotEmptyDirectoryFixture {
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.move(target, isOverride = true)
              __Verify__
              result.value should be (a [IOException])
              dir should exist
            }
          }
      }

      "(for Links)" - {

        "move a link" in new FileWrapperLike_LinkFixture {
          __SetUp__
          val targetPath = createNotExistingFile(suffix = ".lnk")
          val target = asF(targetPath)
          __Exercise__
          val result = sut.move(target)
          __Verify__
          result should be (None)
          link should not (exist)
          targetPath should exist
          text(targetPath) should equal (contentAsString)
          linkTarget should exist
          text(linkTarget) should equal (contentAsString)
        }
      }

      "(for Symbolic Links)" - {

        "move a symbolic link" taggedAs WindowsAdministrated in
          new WindowsAdministratorRequirement {
            new FileWrapperLike_SymbolicLinkFixture {
              __SetUp__
              val targetPath = createNotExistingFile(suffix = ".symlink")
              val target = asF(targetPath)
              __Exercise__
              val result = sut.move(target)
              __Verify__
              result should be(None)
              symbolicLink should not(exist)
              targetPath should exist
              text(targetPath) should equal(contentAsString)
              linkTarget should exist
              text(linkTarget) should equal(contentAsString)
            }
          }
      }
    }

    "copy() method should" - {

      "(for Files)" - {

        "copy a file" in new FileWrapperLike_FileWithContentFixture {
          __SetUp__
          val targetPath = createNotExistingFile()
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copy(target)
          __Verify__
          result should be (None)
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
            path should exist
          }

        "RETURN an Option[IOException] if the target file exists and the 'isOverride' arg is false" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempFile(deleteOnExit = true))
            __Exercise__
            val result = sut.copy(target, isOverride = false)
            __Verify__
            result.value should be(a[IOException])
            path should exist
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

      "(for Directories)" - {

        "copy the directory" in new FileWrapperLike_DirectoryFixture {
          __SetUp__
          val targetPath = createNotExistingDirectory()
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copy(target)
          __Verify__
          result should be (None)
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
            path should exist
          }

        "RETURN an Option[IOException] if the target directory exists and the 'isOverride' arg is false" in
          new FileWrapperLike_FileFixture {
            __SetUp__
            val target = asF(GluinoPath.createTempDirectory(deleteOnExit = true))
            __Exercise__
            val result = sut.copy(target, isOverride = false)
            __Verify__
            result.value should be (a [IOException])
            path should exist
          }

        "copy the directory even if the target directory exists when the 'isOverride' arg is true" in
          new FileWrapperLike_DirectoryFixture {
            __SetUp__
            val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
            val target = asF(targetPath)
            __Exercise__
            val result = sut.copy(target, isOverride = true)
            __Verify__
            result should be (None)
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
              dir should exist
            }
          }

        "RETURN an Option[IOException] if the directory is not empty and the 'isOverride' arg is false" in
          new FileWrapperLike_NotEmptyDirectoryFixture {
            new NotEmptyDirectoryTargetFixture {
              __Exercise__
              val result = sut.copy(target, isOverride = false)
              __Verify__
              result.value should be (a [IOException])
              dir should exist
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
              dir should exist
            }
          }
      }

      "(for Links)" - {

        "copy a link" in new FileWrapperLike_LinkFixture {
          __SetUp__
          val targetPath = createNotExistingFile(suffix = ".lnk")
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copy(target)
          __Verify__
          result should be (None)
          link should exist
          text(link) should equal (contentAsString)
          targetPath should exist
          text(targetPath) should equal (contentAsString)
          linkTarget should exist
          text(linkTarget) should equal (contentAsString)
        }
      }

      "(for Symbolic Links)" - {

        "copy a symbolic link" taggedAs WindowsAdministrated in
          new WindowsAdministratorRequirement {
            new FileWrapperLike_SymbolicLinkFixture {
              __SetUp__
              val targetPath = createNotExistingFile(suffix = ".symlink")
              val target = asF(targetPath)
              __Exercise__
              val result = sut.copy(target)
              __Verify__
              result should be(None)
              symbolicLink should exist
              text(symbolicLink) should equal (contentAsString)
              targetPath should exist
              text(targetPath) should equal(contentAsString)
              linkTarget should exist
              text(linkTarget) should equal(contentAsString)
            }
          }
      }
    }

    "<< operator should" - {

      "write down the specified String to this file" in new FileWrapperLike_FileWithContentFixture {
        __Exercise__
        sut << "fourth line."
        __Verify__
        text(path) should equal(contentAsString + "fourth line.")
      }

      "copy content from the specified file to this one" in new FileWrapperLike_NotExistingFileFixture {
        __Exercise__
        val result = sut << readOnlyPath
        __Verify__
        path should exist
        text(path) should equal(contentAsString)
        readOnlyPath should exist
        text(readOnlyPath) should equal(contentAsString)
      }

      "copy content from the specified file to this one even if this file exists" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut << readOnlyPath
          __Verify__
          path should exist
          text(path) should equal(contentAsString)
          readOnlyPath should exist
          text(readOnlyPath) should equal(contentAsString)
        }

      "append content from the specified file to this one if this has content" in
        new FileWrapperLike_FileWithContentFixture {
          __Exercise__
          val result = sut << readOnlyPath
          __Verify__
          path should exist
          text(path) should equal(contentAsString + contentAsString)
          readOnlyPath should exist
          text(readOnlyPath) should equal(contentAsString)
        }
    }

    "delete() method should" - {

      "delete a file" in new FileWrapperLike_FileFixture{
        __Exercise__
        val result = sut.delete()
        __Verify__
        result should be (None)
        path should not (exist)
      }

      "delete a directory" in new FileWrapperLike_DirectoryFixture {
        __Exercise__
        val result = sut.delete()
        __Verify__
        result should be (None)
        dir should not (exist)
      }

      "delete a link" in new FileWrapperLike_LinkFixture {
        __Exercise__
        val result = sut.delete()
        __Verify__
        result should be (None)
        link should not (exist)
        linkTarget should exist
        text(linkTarget) should equal (contentAsString)
      }

      "delete a symbolic link" taggedAs WindowsAdministrated in
        new WindowsAdministratorRequirement {
          new FileWrapperLike_SymbolicLinkFixture {
            __Exercise__
            val result = sut.delete()
            __Verify__
            result should be(None)
            symbolicLink should not(exist)
            linkTarget should exist
            text(linkTarget) should equal(contentAsString)
          }
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

        """iterate directories in the specified directory
          | if the argument is FileType.Directories (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFile(FileType.Directories)(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("dir1", "dir2", "dir3")
          }

        """iterate files and directories in the specified directory
          | if the argument is FileType.Any (NOT deeply iterate)""".stripMargin in
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

        """iterate files and directories which matches the specified condition
          | in the specified directory (NOT deeply iterate)""".stripMargin in
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

        """iterate files which matches the specified condition in the specified directory
          | if the FileType argument is Files (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatch(FileType.Files, fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("child2.txt")
          }

        """iterate directories which matches the specified condition in the specified directory
          | if the FileType argument is Directories (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatch(FileType.Directories, fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("dir2")
          }

        """iterate files and directories which matches the specified condition in the specified directory
          | if the FileType argument is Any (NOT deeply iterate)""".stripMargin in
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

        """deeply iterate files and directories in the specified directory
          | if the FileType argument is FileType.Any""".stripMargin in
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

        """deeply iterate files and directories in the specified directory
          | if the FileType argument is omitted""".stripMargin in
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

        "visit each directory after its children if visitDirectoryLater is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val files = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse(FileType.Any, visitDirectoryLater = true)(files += _)
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

        "visit each directory before its children if visitDirectoryLater is false (DO deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val files = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileRecurse(FileType.Any, visitDirectoryLater = false)(files += _)
            __Verify__
            var result = fileNames(files)
            result should contain inOrder(sut.fileName, "dir1", "child11.txt")
            result should contain inOrder(sut.fileName, "dir1", "child12.txt")

            result should contain inOrder(sut.fileName, "dir2", "child21.txt")
            result should contain inOrder(sut.fileName, "dir2", "child22.txt")
            result should contain inOrder(sut.fileName, "dir2", "child23.txt")

            result should contain inOrder(sut.fileName, "dir3", "dir31", "child311.txt")
            result should contain inOrder(sut.fileName, "dir3", "dir31", "child312.txt")
          }

        "visit each directory before its children if visitDirectoryLater is omitted" in
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

        """deeply iterate files which matches the specified condition in the specified directory
          | if the FileType argument is Files""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Files, fileNameContains("2"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child2.txt", "child12.txt", "child21.txt", "child22.txt", "child23.txt", "child312.txt")
          }

        """deeply iterate directories which matches the specified condition in the specified directory
          | if the FileType argument is Directories""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Directories, fileNameContains("3"))(result += _)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq(sut.fileName, "dir3", "dir31")
          }

        """iterate files and directories which matches the specified condition in the specified directory
          | if the FileType argument is Any""".stripMargin in
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

        "visit each directory after its children if visitDirectoryLater is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Any, fileNameContains("3"), visitDirectoryLater = true)(result += _)
            __Verify__
            fileNames(result) should contain inOrder("child312.txt", "dir31", "dir3", sut.fileName)
          }

        "visit each directory before its children if visitDirectoryLater is false (DO deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachFileMatchRecurse(FileType.Any, fileNameContains("3"), visitDirectoryLater = false)(result += _)
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31", "child311.txt")
          }

        "visit each directory before its children if visitDirectoryLater is omitted" in
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

        """iterate directories which matches the specified condition
          | in the specified directory (NOT deeply iterate)""".stripMargin in
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

        "visit each directory after its children if visitParentLater is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val dirs = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirRecurse(visitParentLater = true)(dirs += _)
            __Verify__
            val result = fileNames(dirs)
            result should contain inOrder("dir1", sut.fileName)
            result should contain inOrder("dir2", sut.fileName)
            result should contain inOrder("dir31", "dir3", sut.fileName)
          }

        "visit each directory before its children if visitParentLater is false" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val dirs = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirRecurse(visitParentLater = false)(dirs += _)
            __Verify__
            val result = fileNames(dirs)
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

        "visit directory after its children if visitParentLater is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirMatchRecurse(fileNameContains("3"), visitParentLater = true)(result += _)
            __Verify__
            fileNames(result) should contain inOrder("dir31", "dir3", sut.fileName)
          }

        "visit directory before its children if visitParentLater is false" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__
            sut.eachDirMatchRecurse(fileNameContains("3"), visitParentLater = false)(result += _)
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31")
          }

        "visit directory before its children if visitDirectoryLater is omitted" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __SetUp__
            val result = new mutable.MutableList[F]
            __Exercise__ 
            sut.eachDirMatchRecurse(fileNameContains("3"))(result += _)
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31")
          }
      }
    }

    "***** files[Match][Recurse]() *****" - {

      "files{} method should" - {

        "collect files and directories in the specified directory (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.files
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt", "dir1", "dir2", "dir3")
          }
      }

      "files(FileType){} method should" - {

        "collect files in the specified directory if the argument is FileType.Files (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.files(FileType.Files)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt")
          }

        """collect directories in the specified directory
          | if the argument is FileType.Directories (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.files(FileType.Directories)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("dir1", "dir2", "dir3")
          }

        """collect files and directories in the specified directory
          | if the argument is FileType.Any (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.files(FileType.Any)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt", "dir1", "dir2", "dir3")
          }
      }

      "fileMatch(F =>Boolean){} method should" - {

        """collect files and directories which matches the specified condition
          | in the specified directory (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatch(fileNameContains("2"))
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child2.txt", "dir2")
          }
      }

      "filesMatch(FileType, F => Boolean){} method should" - {

        """collect files which matches the specified condition in the specified directory
          | if the FileType argument is Files (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatch(FileType.Files, fileNameContains("2"))
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("child2.txt")
          }

        """collect directories which matches the specified condition in the specified directory
          | if the FileType argument is Directories (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatch(FileType.Directories, fileNameContains("2"))
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("dir2")
          }

        """collect files and directories which matches the specified condition in the specified directory
          | if the FileType argument is Any (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatch(FileType.Any, fileNameContains("2"))
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child2.txt", "dir2")
          }
      }

      "filesRecurse(FileType, Boolean){} method should" - {

        "collect files (deeply iterate) in the specified directory if the FileType argument is Files" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesRecurse(FileType.Files)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt",
                "child11.txt", "child12.txt",
                "child21.txt", "child22.txt", "child23.txt",
                "child31.txt", "child311.txt", "child312.txt")
          }

        """collect directories (deeply iterate) in the specified directory
          | if the FileType argument is Directories""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesRecurse(FileType.Directories)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq(sut.fileName, "dir1", "dir2", "dir3", "dir31")
          }

        """collect files and directories (deeply iterate) in the specified directory
          | if the FileType argument is FileType.Any""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesRecurse(FileType.Any)
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt",
                "child11.txt", "child12.txt",
                "child21.txt", "child22.txt", "child23.txt",
                "child31.txt", "child311.txt", "child312.txt",
                sut.fileName, "dir1", "dir2", "dir3", "dir31")
          }

        """collect files and directories (deeply iterate) in the specified directory
          | if the FileType argument is omitted""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesRecurse()
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child1.txt", "child2.txt", "child3.txt",
                "child11.txt", "child12.txt",
                "child21.txt", "child22.txt", "child23.txt",
                "child31.txt", "child311.txt", "child312.txt",
                sut.fileName, "dir1", "dir2", "dir3", "dir31")
          }

        "visit each directory after its children if visitDirectoryLater is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val files = sut.filesRecurse(FileType.Any, visitDirectoryLater = true)
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

        "visit each directory before its children if visitDirectoryLater is false (DO deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val files = sut.filesRecurse(FileType.Any, visitDirectoryLater = false)
            __Verify__
            var result = fileNames(files)
            result should contain inOrder(sut.fileName, "dir1", "child11.txt")
            result should contain inOrder(sut.fileName, "dir1", "child12.txt")

            result should contain inOrder(sut.fileName, "dir2", "child21.txt")
            result should contain inOrder(sut.fileName, "dir2", "child22.txt")
            result should contain inOrder(sut.fileName, "dir2", "child23.txt")

            result should contain inOrder(sut.fileName, "dir3", "dir31", "child311.txt")
            result should contain inOrder(sut.fileName, "dir3", "dir31", "child312.txt")
          }

        "visit each directory before its children if visitDirectoryLater is omitted" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val files = sut.filesRecurse(FileType.Any)
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


      "filesMatchRecurse(F => Boolean, FileType, Boolean){} method should" - {

        """collect files which matches the specified condition in the specified directory
          | if the FileType argument is Files""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatchRecurse(FileType.Files, fileNameContains("2"))
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child2.txt", "child12.txt", "child21.txt", "child22.txt", "child23.txt", "child312.txt")
          }

        """map directories which matches the specified condition in the specified directory
          | if the FileType argument is Directories""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatchRecurse(FileType.Directories, fileNameContains("3"))
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq(sut.fileName, "dir3", "dir31")
          }

        """collect files and directories which matches the specified condition in the specified directory
          | if the FileType argument is Any""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatchRecurse(FileType.Any, fileNameContains("3"))
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq("child3.txt", "child23.txt",
                "child31.txt", "child311.txt", "child312.txt",
                sut.fileName, "dir3", "dir31")
          }

        "visit each directory after its children if visitDirectoryLater is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatchRecurse(FileType.Any, fileNameContains("3"), visitDirectoryLater = true)
            __Verify__
            fileNames(result) should contain inOrder("child312.txt", "dir31", "dir3", sut.fileName)
          }

        "visit each directory before its children if visitDirectoryLater is false (DO deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatchRecurse(FileType.Any, fileNameContains("3"), visitDirectoryLater = false)
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31", "child311.txt")
          }

        "visit each directory before its children if visitDirectoryLater is omitted" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.filesMatchRecurse(FileType.Any, fileNameContains("3"))
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31", "child312.txt")
          }
      }
    }

    "***** dirs[Match][Recurse] *****" - {

      "dirs{} method should" - {

        "collect directories in the specified directory (NOT deeply iterate)" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.dirs
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("dir1", "dir2", "dir3")
          }
      }

      "dirsMatch(F => Boolean){} method should" - {

        """collect directories which matches the specified condition in the specified directory
          | (NOT deeply iterate)""".stripMargin in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.dirsMatch(fileNameContains("2"))
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq("dir2")
          }
      }

      "dirsRecurse{} method should" - {

        "collect directories (deeply iterate) in the specified directory" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.dirsRecurse
            __Verify__
            fileNames(result) should contain theSameElementsAs
              Seq(sut.fileName, "dir1", "dir2", "dir3", "dir31")
          }

        "visit each directory before its children." in
          new FileWrapperLike_ReadOnlyDirFixture {
              __Exercise__
            val dirs = sut.dirsRecurse
            __Verify__
            val result = fileNames(dirs)
            result should contain inOrder (sut.fileName, "dir1")
            result should contain inOrder (sut.fileName, "dir2")
            result should contain inOrder (sut.fileName, "dir3", "dir31")
          }
      }

      "dirsRecurse(Boolean){} method should" - {

        "visit each directory after its children if visitParentLater is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val dirs = sut.dirsRecurse(visitParentLater = true)
            __Verify__
            val result = fileNames(dirs)
            result should contain inOrder("dir1", sut.fileName)
            result should contain inOrder("dir2", sut.fileName)
            result should contain inOrder("dir31", "dir3", sut.fileName)
          }

        "visit each directory before its children if visitParentLater is false" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val dirs = sut.dirsRecurse(visitParentLater = false)
            __Verify__
            val result = fileNames(dirs)
            result should contain inOrder(sut.fileName, "dir1")
            result should contain inOrder(sut.fileName, "dir2")
            result should contain inOrder(sut.fileName, "dir3", "dir31")
          }
      }


      "dirsMatchRecurse(F => Boolean, Boolean){} method should" - {

        "collect directories which matches the specified condition in the specified directory" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.dirsMatchRecurse(fileNameContains("3"))
            __Verify__
            fileNames(result) should contain theSameElementsAs Seq(sut.fileName, "dir3", "dir31")
          }

        "visit directory after its children if visitParentLater is true" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.dirsMatchRecurse(fileNameContains("3"), visitParentLater = true)
            __Verify__
            fileNames(result) should contain inOrder("dir31", "dir3", sut.fileName)
          }

        "visit directory before its children if visitParentLater is false" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.dirsMatchRecurse(fileNameContains("3"), visitParentLater = false)
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31")
          }

        "visit directory before its children if visitDirectoryLater is omitted" in
          new FileWrapperLike_ReadOnlyDirFixture {
            __Exercise__
            val result = sut.dirsMatchRecurse(fileNameContains("3"))
            __Verify__
            fileNames(result) should contain inOrder(sut.fileName, "dir3", "dir31")
          }
      }
    }

    "traverse() method should" - {

      "traverse directory structure" in new FileWrapperLike_DirectoryWithFilesFixture {
        __SetUp__
        val result = mutable.MutableList[F]()
        __Exercise__
        sut.traverse(){ f =>
          result += f
          FileVisitResult.CONTINUE
        }
        __Verify__
        fileNames(result) should contain theSameElementsInOrderAs
          Seq("dir1", "child11.txt", "child12.txt",
            "dir2", "child21.txt", "child22.txt", "child23.txt",
            "dir3", "dir31", "child311.txt", "child312.txt", "child31.txt",
            "child1.txt", "child2.txt", "child3.txt")
      }

      "(fileType arg)" - {

        "traverse all files and directors if the 'fileType' arg is set to FileType.Any" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(fileType=FileType.Any){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt",
                "dir3", "dir31", "child311.txt", "child312.txt", "child31.txt",
                "child1.txt", "child2.txt", "child3.txt")
          }

        "traverse only files if the 'fileType' arg is set to FileType.Files" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(fileType=FileType.Files){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("child11.txt", "child12.txt",
                "child21.txt", "child22.txt", "child23.txt",
                "child311.txt", "child312.txt", "child31.txt",
                "child1.txt", "child2.txt", "child3.txt")
        }

        "traverse only directories if the 'fileType' arg is set to FileType.Directories" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(fileType=FileType.Directories){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "dir2", "dir3", "dir31")
          }
      }

      "(filter/nameFilter/excludeFilter/excludeNameFilter arg)" - {

        "traverse files and directors which matches filter if the 'filer' arg is set" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(filter=fileNameContains("2")) { f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("child12.txt", "dir2", "child21.txt", "child22.txt",
                "child23.txt", "child312.txt", "child2.txt")
          }

        "traverse files and directors whose name matches nameFilter if the 'nameFiler' arg is set" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(nameFilter= s => s.contains("3")) { f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("child23.txt", "dir3", "dir31", "child311.txt",
                "child312.txt", "child31.txt", "child3.txt")
          }

        "traverse files and directors which does not match filter if the 'excludeFiler' arg is set" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(excludeFilter=fileNameContains("2")) { f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "dir3", "dir31", "child311.txt",
                "child31.txt", "child1.txt", "child3.txt")
          }

        "traverse files and directors whose name does not match filter if the 'excludeNameFiler' arg is set" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(excludeNameFilter= s => s.contains("3")) { f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt", "dir2", "child21.txt",
                "child22.txt", "child1.txt", "child2.txt")
          }
      }

      "(preDir/postDir arg)" - {

        "execute 'preDir' function before visit each directory" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[String]()
            __Exercise__
            sut.traverse(fileType=FileType.Directories,
                         preDir={ f => result += ("pre-" + wrap(f).fileName); FileVisitResult.CONTINUE}) { f =>
              result += wrap(f).fileName
              FileVisitResult.CONTINUE
            }
            __Verify__
            result should contain theSameElementsInOrderAs
              Seq("pre-dir1", "dir1", "pre-dir2", "dir2", "pre-dir3", "dir3", "pre-dir31", "dir31")
          }

        "execute 'preDir' function before visit each directory even if only files are visited" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[String]()
            __Exercise__
            sut.traverse(fileType=FileType.Files,
                         preDir={ f => result += wrap(f).fileName; FileVisitResult.CONTINUE}) { f =>
              FileVisitResult.CONTINUE
            }
            __Verify__
            result should contain theSameElementsInOrderAs
              Seq("dir1", "dir2", "dir3", "dir31")
          }

        "execute 'postDir' function after visit each directory" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[String]()
            __Exercise__
            sut.traverse(fileType=FileType.Directories,
              postDir={ f => result += ("post-" + wrap(f).fileName); FileVisitResult.CONTINUE}) { f =>
              result += wrap(f).fileName
              FileVisitResult.CONTINUE
            }
            __Verify__
            result should contain theSameElementsInOrderAs
              Seq("dir1", "post-dir1", "dir2", "post-dir2", "dir3", "dir31", "post-dir31", "post-dir3")
          }

        "execute 'postDir' function after visit each directory even if only files are visited" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[String]()
            __Exercise__
            sut.traverse(fileType=FileType.Files,
                         postDir={ f => result += wrap(f).fileName; FileVisitResult.CONTINUE}) { f =>
              FileVisitResult.CONTINUE
            }
            __Verify__
            result should contain theSameElementsInOrderAs
              Seq("dir1", "dir2", "dir31", "dir3")
          }
      }

      "(visitRoot/preRoot/postRoot arg)" - {

        "visit the root directory if the 'visitRoot' arg is true" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(visitRoot = true) { f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq(sut.fileName,
                "dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt",
                "dir3", "dir31", "child311.txt", "child312.txt", "child31.txt",
                "child1.txt", "child2.txt", "child3.txt")
          }

        """execute 'preDir' function before visiting the root directory if the 'preRoot' arg is true
          |(the root directory is not visited if the arg 'visitRoot' is not true)""".stripMargin in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[String]()
            __Exercise__
            sut.traverse(fileType=FileType.Directories,
                         preDir={ f => result += ("pre-" + wrap(f).fileName); FileVisitResult.CONTINUE},
                         preRoot=true) { f =>
              result += wrap(f).fileName
              FileVisitResult.CONTINUE
            }
            __Verify__
            result should contain theSameElementsInOrderAs
              Seq("pre-"+sut.fileName, "pre-dir1", "dir1",
                "pre-dir2", "dir2", "pre-dir3", "dir3", "pre-dir31", "dir31")
          }

        "execute 'postDir' function after visiting the root directory if the 'postRoot' arg is true" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[String]()
            __Exercise__
            sut.traverse(fileType=FileType.Directories,
                         postDir={ f => result += ("post-" + wrap(f).fileName); FileVisitResult.CONTINUE},
                         postRoot=true) { f =>
              result += wrap(f).fileName
              FileVisitResult.CONTINUE
            }
            __Verify__
            result should contain theSameElementsInOrderAs
              Seq("dir1", "post-dir1", "dir2", "post-dir2",
                "dir3", "dir31", "post-dir31", "post-dir3", "post-"+sut.fileName)
          }
      }

      "(maxDepth arg)" - {

        "visit only child files and directories under the root directory if the 'maxDepth' arg is 0" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(maxDepth = 0) { f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "dir2",  "dir3", "child1.txt", "child2.txt", "child3.txt")
          }

          "visit files and directories upto the specified depth if the 'maxDepth' arg is set" in
            new FileWrapperLike_DirectoryWithFilesFixture {
              __SetUp__
              val result = mutable.MutableList[F]()
              __Exercise__
              sut.traverse(maxDepth = 1) { f =>
                result += f
                FileVisitResult.CONTINUE
              }
              __Verify__
              fileNames(result) should contain theSameElementsInOrderAs
                Seq("dir1", "child11.txt", "child12.txt",
                  "dir2", "child21.txt", "child22.txt", "child23.txt",
                  "dir3", "dir31", "child31.txt",
                  "child1.txt", "child2.txt", "child3.txt")
            }
      }

      "(sort)" - {

        "iterate sibling files and directories by the specified order" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            val order = (a: F, b: F) => {
              val x = wrap(a)
              val y = wrap(b)
              if (x.isFile != y.isFile) x.isFile > y.isFile
              else x.fileName < y.fileName
            }
            __Exercise__
            sut.traverse(sort = order) { f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("child1.txt", "child2.txt", "child3.txt",
                "dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt",
                "dir3", "child31.txt", "dir31", "child311.txt", "child312.txt")
          }
      }

      "(return FileVisitResult)" - {

        // TERMINATE
        "terminate traversing if the consumer return FileVisitResult.TERMINATE" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(){ f =>
              result += f
              val w = wrap(f)
              if(w.isDirectory && w.fileName.contains("2"))
                FileVisitResult.TERMINATE
              else
                FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt", "dir2")
          }

        "terminate traversing if the preDir return FileVisitResult.TERMINATE" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            val preDir = { f: F =>
              if(wrap(f).fileName.contains("2"))
                FileVisitResult.TERMINATE
              else
                FileVisitResult.CONTINUE
            }
            __Exercise__
            sut.traverse(preDir = preDir){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt")
          }

        "terminate traversing if the postDir return FileVisitResult.TERMINATE" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            val postDir = { f: F =>
              if(wrap(f).fileName.contains("2"))
                FileVisitResult.TERMINATE
              else
                FileVisitResult.CONTINUE
            }
            __Exercise__
            sut.traverse(postDir = postDir){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt")
          }

        // SKIP_SUBTREE
        "skip traversing subtree if the consumer return FileVisitResult.SKIP_SUBTREE" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(){ f =>
              result += f
              val w = wrap(f)
              if(w.isDirectory && w.fileName.contains("31"))
                FileVisitResult.SKIP_SUBTREE
              else
                FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt",
                "dir3", "dir31", "child31.txt",
                "child1.txt", "child2.txt", "child3.txt")
          }

        "skip traversing subtree if the preDir return FileVisitResult.SKIP_SUBTREE" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            val preDir = { f: F =>
              if(wrap(f).fileName.contains("31"))
                FileVisitResult.SKIP_SUBTREE
              else
                FileVisitResult.CONTINUE
            }
            __Exercise__
            sut.traverse(preDir = preDir){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt",
                "dir3", "dir31", "child31.txt",
                "child1.txt", "child2.txt", "child3.txt")
          }

        """skip traversing subtree if the postDir return FileVisitResult.SKIP_SUBTREE
          |(this does not effect to result because the subtree has already been traversed when postDir is called)
          |""".stripMargin in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            val postDir = { f: F =>
              if(wrap(f).fileName.contains("31"))
                FileVisitResult.SKIP_SUBTREE
              else
                FileVisitResult.CONTINUE
            }
            __Exercise__
            sut.traverse(postDir = postDir){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt",
                "dir3", "dir31", "child311.txt", "child312.txt", "child31.txt",
                "child1.txt", "child2.txt", "child3.txt")
          }

        // SKIP_SIBLINGS
        "skip traversing siblings if the consumer return FileVisitResult.SKIP_SIBLINGS" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            __Exercise__
            sut.traverse(){ f =>
              result += f
              val w = wrap(f)
              if(w.isDirectory && w.fileName.contains("2"))
                FileVisitResult.SKIP_SIBLINGS
              else
                FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt")
          }

        "skip traversing siblings if the preDir return FileVisitResult.SKIP_SIBLINGS" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            val preDir = { f: F =>
              if(wrap(f).fileName.contains("2"))
                FileVisitResult.SKIP_SIBLINGS
              else
                FileVisitResult.CONTINUE
            }
            __Exercise__
            sut.traverse(preDir = preDir){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt")
          }

        "skip traversing siblings if the postDir return FileVisitResult.SKIP_SIBLINGS" in
          new FileWrapperLike_DirectoryWithFilesFixture {
            __SetUp__
            val result = mutable.MutableList[F]()
            val postDir = { f: F =>
              if(wrap(f).fileName.contains("2"))
                FileVisitResult.SKIP_SIBLINGS
              else
                FileVisitResult.CONTINUE
            }
            __Exercise__
            sut.traverse(postDir = postDir){ f =>
              result += f
              FileVisitResult.CONTINUE
            }
            __Verify__
            fileNames(result) should contain theSameElementsInOrderAs
              Seq("dir1", "child11.txt", "child12.txt",
                "dir2", "child21.txt", "child22.txt", "child23.txt")
          }
      }
    }
  }

  "***** Directory Operations *****" - {

    "moveDir() method should" - {

      "move a directory even if not empty" in new FileWrapperLike_DirectoryWithFilesFixture {
        __SetUp__
        val targetPath = createNotExistingDirectory()
        val target = asF(targetPath)
        __Exercise__
        val result = sut.moveDir(target)
        __Verify__
        result should be (None)

        targetPath should exist
        (targetPath resolve "child1.txt") should exist

        dir should not (exist)
        __TearDown__
        wrap(target).deleteDir()
      }

      """RETURN an Option[IOException] if the target directory already exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.moveDir(target)
          __Verify__
          result.value should be (an [IOException])

          targetPath should exist

          dir should exist
          (dir resolve "child1.txt") should exist
        }

      """RETURN an Option[IOException] if the target directory already exists
        | and the 'isOverride' arg is false""".stripMargin in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.moveDir(target, isOverride = false)
          __Verify__
          result.value should be (an [IOException])

          targetPath should exist

          dir should exist
          (dir resolve "child1.txt") should exist
        }

      "move the directory if the target directory already exists when the 'isOverride' arg is true" in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.moveDir(target, isOverride = true)
          __Verify__
          result should be (None)

          targetPath should exist
          (targetPath resolve "child1.txt") should exist

          dir should not (exist)
          __TearDown__
          wrap(target).deleteDir()
        }

      "do nothing if the source directory does not exist" in {
        __SetUp__
        val dir = createNotExistingDirectory()
        val sut = newFileWrapperLike(dir)
        val targetPath = createNotExistingDirectory()
        val target = asF(targetPath)
        __Exercise__
        val result = sut.moveDir(target)
        __Verify__
        result should be (None)
        dir should not (exist)
        targetPath should not (exist)
      }

      "RETURN an Option[IOException] if the source is a file (not a directory)" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val targetPath = createNotExistingDirectory()
          val target = asF(targetPath)
          __Exercise__
          val result = sut.moveDir(target)
          __Verify__
          result.value should be (a [IOException])
          path should exist
          targetPath should not (exist)
        }
    }

    "copyDir() method should" - {

      "copy a directory even if not empty" in new FileWrapperLike_DirectoryWithFilesFixture {
        __SetUp__
        val targetPath = createNotExistingDirectory()
        val target = asF(targetPath)
        __Exercise__
        val result = sut.copyDir(target)
        __Verify__
        result should be (None)

        targetPath should exist
        (targetPath resolve "child1.txt") should exist

        dir should exist
        (dir resolve "child1.txt") should exist
        __TearDown__
        wrap(target).deleteDir()
      }

      """RETURN an Option[IOException] if the target directory already exists
        | and the 'isOverride' arg is omitted""".stripMargin in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copyDir(target)
          __Verify__
          result.value should be (an [IOException])

          targetPath should exist

          dir should exist
          (dir resolve "child1.txt") should exist
        }

      "RETURN an Option[IOException] if the target directory already exists and the 'isOverride' arg is false" in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copyDir(target, isOverride = false)
          __Verify__
          result.value should be (an [IOException])

          targetPath should exist

          dir should exist
          (dir resolve "child1.txt") should exist
        }

      "copy the directory if the target directory already exists when the 'isOverride' arg is true" in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __SetUp__
          val targetPath = GluinoPath.createTempDirectory(deleteOnExit = true)
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copyDir(target, isOverride = true)
          __Verify__
          result should be (None)

          targetPath should exist
          (targetPath resolve "child1.txt") should exist

          dir should exist
          (dir resolve "child1.txt") should exist
          __TearDown__
          wrap(target).deleteDir()
        }

      "do nothing if the source directory does not exist" in {
        __SetUp__
        val dir = createNotExistingDirectory()
        val sut = newFileWrapperLike(dir)
        val targetPath = createNotExistingDirectory()
        val target = asF(targetPath)
        __Exercise__
        val result = sut.copyDir(target)
        __Verify__
        result should be (None)
        dir should not (exist)
        targetPath should not (exist)
      }

      "RETURN an Option[IOException] if the source is a file (not a directory)" in
        new FileWrapperLike_FileFixture {
          __SetUp__
          val targetPath = createNotExistingDirectory()
          val target = asF(targetPath)
          __Exercise__
          val result = sut.copyDir(target)
          __Verify__
          result.value should be (a [IOException])
          path should exist
          targetPath should not (exist)
        }
    }

    "deleteDir() method should" - {

      "delete a directory even if not empty" in
        new FileWrapperLike_DirectoryWithFilesFixture {
          __Exercise__
          val result = sut.deleteDir()
          __Verify__
          result should be (None)
          dir should not (exist)
        }

      "do nothing if the source directory does not exist" in {
        __SetUp__
        val dir = createNotExistingDirectory()
        val sut = newFileWrapperLike(dir)
        __Exercise__
        val result = sut.deleteDir()
        __Verify__
        result should be (None)
        dir should not (exist)
      }

      "RETURN an Option[IOException] if the source is a file (not a directory)" in
        new FileWrapperLike_FileFixture {
          __Exercise__
          val result = sut.deleteDir()
          __Verify__
          result.value should be (a [IOException])
          path should exist
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
