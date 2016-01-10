package org.waman.gluino.nio

import java.nio.file.{NoSuchFileException, Files}
import java.nio.file.attribute.PosixFilePermission

import scala.collection.JavaConversions._

import org.waman.gluino.io.GluinoIOCustomSpec
import org.waman.scalatest_util.{WindowsAdministrated, ImplicitConversion, PosixFileSystemSpecific}

class FilesCategorySpec extends GluinoIOCustomSpec with FilesCategory with AttributeConverter{

  "Implicit Conversion" taggedAs ImplicitConversion ignore {

    noException should be thrownBy {
      convertImplicitly[FilesDelegate](readOnlyPath)
    }
  }

  "***** Creation *****" - {

    "createFile() method should" - {

      "create a File without args" in new NotExistingFileFixture {
        __Exercise__
        path.createFile()
        __Verify__
        path should exist
        Files.isRegularFile(path) should equal (true)
      }

      // TODO
    }

    "createDirectory() method should" - {

      "create a Directory without args" in new NotExistingDirectoryFixture {
        __Exercise__
        dir.createDirectory()
        __Verify__
        dir should exist
        Files.isDirectory(dir) should equal (true)
      }

      // TODO
    }

    "createDirectories() method should" - {

      "create Directory hierarchy without args" in new NotExistingDirectoryFixture {
        __SetUp__
        val sut = dir.resolve("child")
        __Exercise__
        sut.createDirectories()
        __Verify__
        sut should exist
        Files.isDirectory(sut) should equal (true)
      }

      // TODO
    }

    "createLink(target: Path) method should create a link" in {
      __SetUp__
      val sut = createNotExistingFile(suffix = ".lnk")
      __Exercise__
      sut.createLink(readOnlyPath)
      __Verify__
      sut should exist
      text(sut) should equal (contentAsString)
    }

    "createSymbolicLink() method should" - {

      "create a symbolic link" taggedAs WindowsAdministrated in new WindowsAdministratorRequirement {
        __SetUp__
        val sut = createNotExistingFile(suffix = ".symlink")
        __Exercise__
        sut.createSymbolicLink(readOnlyPath)
        __Verify__
        sut should exist
        text(sut) should equal (contentAsString)
      }

      // TODO
    }
  }
   
  "***** Temporal File *****" - {

    "createTempFile() method should" - {
      
      "create a temporary file without args" in new DirectoryFixture {
        __Exercise__
        val sut = dir.createTempFile()
        __Verify__
        sut should exist
        Files.isRegularFile(sut) should equal (true)
      }

      "create a temporary file with prefix specified" in new DirectoryFixture {
        __Exercise__
        val sut = dir.createTempFile(prefix = "files-")
        __Verify__
        sut should exist
        Files.isRegularFile(sut) should equal (true)
        sut.getFileName.toString should startWith ("files-")
      }

      "create a temporary file with suffix specified" in new DirectoryFixture {
        __Exercise__
        val sut = dir.createTempFile(suffix = ".txt")
        __Verify__
        sut should exist
        Files.isRegularFile(sut) should equal (true)
        sut.getFileName.toString should endWith (".txt")
      }

      // TODO
    }

    "createTempDirectory() method should" - {

      "create a temporary directory without args" in new DirectoryFixture {
        __Exercise__
        val sut = dir.createTempDirectory()
        __Verify__
        sut should exist
        Files.isDirectory(sut) should equal (true)
      }

      "create a temporary file with prefix specified" in new DirectoryFixture {
        __Exercise__
        val sut = dir.createTempDirectory(prefix = "files-")
        __Verify__
        sut should exist
        sut.getFileName.toString should startWith ("files-")
        Files.isDirectory(sut) should equal (true)
      }

      // TODO
    }
  }

  "***** File Operations *****" - {

    "Exist/Delete" - {

      "exists() method should" - {

        "return true if the file exists" in {
          __Verify__
          readOnlyPath.exists() should equal (true)
        }

        "return false if the file does not exist" in new NotExistingFileFixture {
          __Verify__
          path.exists() should equal (false)
        }

        "return true if the directory exists" in new DirectoryFixture {
          __Verify__
          dir.exists() should equal (true)
        }

        "return false if the directory does not exist" in new NotExistingDirectoryFixture {
          __Verify__
          dir.exists() should equal (false)
        }

        // TODO
      }

      "notExists()"- {

        "return false if the file exists" in {
          __Verify__
          readOnlyPath.notExists() should equal (false)
        }

        "return true if the file does not exist" in new NotExistingFileFixture {
          __Verify__
          path.notExists() should equal (true)
        }

        "return false if the directory exists" in new DirectoryFixture {
          __Verify__
          dir.notExists() should equal (false)
        }

        "return true if the directory does not exist" in new NotExistingDirectoryFixture {
          __Verify__
          dir.notExists() should equal (true)
        }

        // TODO
      }

      "delete() method should" - {

        "delete the specified file" in new FileFixture {
          __Exercise__
          path.delete()
          __Verify__
          path should not (exist)
        }

        "throw a NoSuchFileException if the specified file does not exist" in new NotExistingFileFixture {
          __Verify__
          a [NoSuchFileException] should be thrownBy{
            path.delete()
          }
        }

        "delete the specified directory" in new DirectoryFixture {
          __Exercise__
          dir.delete()
          __Verify__
          dir should not (exist)
        }

        "throw a NoSuchFileException if the specified directory does not exist" in new NotExistingDirectoryFixture {
          __Verify__
          a [NoSuchFileException] should be thrownBy{
            dir.delete()
          }
        }
      }

      "deleteIfExists() method should" - {

        "delete the specified file if exists" in new FileFixture {
          __Exercise__
          path.deleteIfExists()
          __Verify__
          path should not (exist)
        }

        "do nothing if the specified file does not exist" in new NotExistingFileFixture {
          __Exercise__
          path.deleteIfExists()
          __Verify__
          path should not (exist)
        }

        "delete the specified directory if exists" in new DirectoryFixture {
          __Exercise__
          dir.deleteIfExists()
          __Verify__
          dir should not (exist)
        }

        "do nothing if the specified directory does not exist" in new NotExistingDirectoryFixture {
          __Exercise__
          dir.deleteIfExists()
          __Verify__
          dir should not (exist)
        }
      }
    }

    "Copy/Move" - {

      "copyFromStream(in: InputStream, options: Set[CopyOption] = Nil) method should" - {
//        Files.copy(in, path, options.toArray: _*)
      }

      "copyToStream(out: OutputStream" - {
//        Files.copy(path, out)
      }

      "copyFrom(source: Path, options: Set[CopyOption] = Nil) method should" - {
//        Files.copy(source, path, options.toArray: _*)
      }

      "copyTo(target: Path, options: Set[CopyOption] = Nil) method should" - {
//        Files.copy(path, target, options.toArray: _*)
      }

      "moveFrom(source: Path, options: Set[CopyOption] = Nil) method should" - {
//        Files.move(source, path, options.toArray: _*)
      }

      "moveTo(target: Path, options: Set[CopyOption] = Nil) method should" - {
//        Files.move(path, target, options.toArray: _*)
      }
    }

    "readSymbolicLink() method should" - {

      "read context of the target file specified the symbolic link" taggedAs WindowsAdministrated in
        new WindowsAdministratorRequirement {
          new SymbolicLinkFixture {
            __Exercise__
            val sut = symbolicLink.readSymbolicLink()
            __Verify__
            sut should equal(readOnlyPath)
          }
        }
      }
  }

  "***** Read/Write *****" - {

    "isReadable" - {
//      Files.isReadable(path)
    }

    "isWritable" - {
//      Files.isWritable(path)
    }

    "Byte" - {

      "readAllBytes() method should" - {

        "read all byte in the file" in {
          __Exercise__
          val sut = readOnlyPathISO2022.readAllBytes()
          __Verify__
          new String(sut, ISO2022) should equal(contentAsStringISO2022)
        }
      }

      "writeBytes() method should" - {

        "write bytes to the file" in new FileFixture {
          __Exercise__
          path.writeBytes(contentAsStringISO2022.getBytes(ISO2022))
          __Verify__
          text(path, ISO2022) should equal (contentAsStringISO2022)
        }
      }
    }
    
    "InputStream/OutputStream" - {

      "newInputStream(options: Set[OpenOption]) method should" - {
//        Files.newInputStream(path, options.toArray: _*)
      }

      "newOutputStream(options: Set[OpenOption]) method should" - {
//        Files.newOutputStream(path, options.toArray: _*)
      }
    }

    "ByteChannel" - {

      "newByteChannel(options: Set[OpenOption] = Nil, attributes: Seq[FileAttribute[_]] = Nil) method should" - {
//        Files.newByteChannel(path, options, attributes: _*)
      }

      "readAllLines(charset: Charset = GluinoIO.defaultCharset" - {
//        Files.readAllLines(path, charset)
      }

      "write(lines: Seq[String], charset: Charset = GluinoIO.defaultCharset, options: Set[OpenOption] = Nil) method should" - {
//        Files.write(path, lines, charset, options.toArray: _*)
      }
    }

    "BufferedReader/BufferedWriter) method should" - {

      "newBufferedReader(charset: Charset = GluinoIO.defaultCharset" - {
//        Files.newBufferedReader(path, charset)
      }

      "newBufferedWriter(charset: Charset = GluinoIO.defaultCharset, options: Set[OpenOption] = Nil) method should" - {
//        Files.newBufferedWriter(path, charset, options.toArray: _*)
      }
    }

    "Stream" - {

      "lines(consumer: Stream[String] => Unit) method should" - {
//        lines()(consumer)
      }
  
      "lines(charset: Charset = GluinoIO.defaultCharset)(consumer: Stream[String] => Unit) method should" - {
//        val lines = Files.lines(path, charset)
//        try {
//          consumer(lines)
//        } finally {
//          if (lines != null)
//            lines.close()
//        }
      }
    }
  }

  "***** Attributes *****" - {

    "Boolean Properties) method should" - {

      "isRegularFile" - {
//        Files.isRegularFile(path)
      }

      "isRegularFile(options: Set[LinkOption]" - {
//        Files.isRegularFile(path, options.toArray: _*)
      }

      "isDirectory" - {
//        Files.isDirectory(path)
      }

      "isDirectory(options: Set[LinkOption]) method should" - {
//        Files.isDirectory(path, options.toArray: _*)
      }

      "isExecutable" - {
//        Files.isExecutable(path)
      }

      "isHidden" - {
//        Files.isHidden(path)
      }

      "isSameFile(path2: Path) method should" - {
//        Files.isSameFile(path, path2)
      }

      "isSymbolicLink" taggedAs WindowsAdministrated in new WindowsAdministratorRequirement {
        new SymbolicLinkFixture {
          __Verify__
          symbolicLink.isSymbolicLink should equal (true)
        }
      }
    }
  }

  "size method should return size of the file" in {
    __Verify__
    readOnlyBigPath.size should equal (2600)
  }

  "lastModified" - {

    "lastModifiedTime" - {
//      Files.getLastModifiedTime(path)
    }

    "getLastModifiedTime(options: Set[LinkOption] = Nil) method should" - {
//      Files.getLastModifiedTime(path)
    }

    "lastModifiedTime_=(fileTime: FileTime) method should" - {
//      Files.setLastModifiedTime(path, fileTime)
    }

    "lastModifiedTime_=(time: Long) method should" - {
//      lastModifiedTime_=(FileTime.fromMillis(time))
    }
  }

  "Owner" - {

    "owner" - {
//      Files.getOwner(path)
    }

    "getOwner(options: Set[LinkOption] = Nil) method should" - {
//      Files.getOwner(path, options.toArray: _*)
    }

    "owner_=(owner: UserPrincipal) method should" - {
//      Files.setOwner(path, owner)
    }

    "owner_=(owner: String) method should" - {
//      Files.setOwner(path, FileSystems.getDefault.getUserPrincipalLookupService.lookupPrincipalByName(owner))
    }

    "groupOwner_=(group: String) method should" - {
//      Files.setOwner(path, FileSystems.getDefault.getUserPrincipalLookupService.lookupPrincipalByGroupName(group))
    }
  }

  "Posix File Permissions" - {
    
    "posixFilePermissions(options: Set[LinkOption] = Nil)" taggedAs PosixFileSystemSpecific in
      new PosixFileSystemRequirement {
        new FileFixture {
          __Exercise__
          val sut: Set[PosixFilePermission] = path.posixFilePermissions()
          __Verify__
          sut should contain theSameElementsAs Files.getPosixFilePermissions(path)
        }
      }
    
    "posixFilePermissions_=(permissions: Set[PosixFilePermission]) method should" - {
//      Files.setPosixFilePermissions(path, permissions)
    }
  }

  "File Attribute" - {
    
    "getAttribute(attribute: String, options: Set[LinkOption] = Nil) method should" - {
//      Files.getAttribute(path, attribute, options.toArray: _*)
    }

    "setAttribute(attribute: String, value: Any, options: Set[LinkOption] = Nil) method should" - {
//      Files.setAttribute(path, attribute, value, options.toArray: _*)
    }

    "readAttributes(attributes: String, options: Set[LinkOption] = Nil) method should" - {
//      mapAsScalaMap(Files.readAttributes(path, attributes, options.toArray: _*)).toMap
    }

    "readAttribute[A <: BasicFileAttributes](attributeType: Class[A], options: Set[LinkOption] = Nil" - {
//      Files.readAttributes(path, attributeType, options.toArray: _*)
    }

    "getFileAttributeView[V <: FileAttributeView](attributeType: Class[V], options: Set[LinkOption] = Nil" - {
//      Files.getFileAttributeView(path, attributeType, options.toArray: _*)
    }
  }

  "***** Directory Stream *****" - {

    "withDirectoryStream(consumer: DirectoryStream[Path] => Unit) method should" - {
//      consumeDirectoryStream(Files.newDirectoryStream(path), consumer)
    }

    "withDirectoryStream(filter: DirectoryStream.Filter[_ >: Path])(consumer: DirectoryStream[Path] => Unit" - {
//      consumeDirectoryStream(Files.newDirectoryStream(path, filter), consumer)
    }

    "withDirectoryStream(glob: String)(consumer: DirectoryStream[Path] => Unit" - {
//      consumeDirectoryStream(Files.newDirectoryStream(path, glob), consumer)
    }

    "list(consumer: Stream[Path] => Unit) method should" - {
//      val list = Files.list(path)
//      try {
//        consumer(list)
//      } finally {
//        list.close()
//      }
    }
  }
  
  "***** walk file tree *****" - {

    "walkFileTree(options: Set[FileVisitOption] = Nil, maxDepth: Int = Integer.MAX_VALUE, visitor: FileVisitor[_ >: Path]) method should" - {
//      Files.walkFileTree(path, options, maxDepth, visitor)
    }

    "find(matcher: (Path, BasicFileAttributes) => Boolean, maxDepth: Int = Integer.MAX_VALUE, options: Set[FileVisitOption] = Nil)(consumer: Stream[Path] => Unit) method should" - {
//      val stream = Files.find(path, maxDepth, matcher, options.toArray: _*)
//      try {
//        consumer(stream)
//      } finally {
//        if (stream != null)
//          stream.close()
//      }
    }

    "walk(consumer: Stream[Path] => Unit) method should" - {
//      walk()(consumer)
    }

    "walk(maxDepth: Int = Integer.MAX_VALUE, options: Set[FileVisitOption] = Nil)(consumer: Stream[Path] => Unit" - {
//      val stream = Files.walk(path, maxDepth, options.toArray: _*)
//      try {
//        consumer(stream)
//      } finally {
//        if (stream != null)
//          stream.close()
//      }
    }
  }

  "***** File System etc. *****" - {
    
    "probeContentType() method should return a mime type like 'text/javascript'" in {
      __SetUp__
      val path = Files.createTempFile(null, ".js")
      __Exercise__
      val sut = path.probeContentType()
      __Verify__
      sut should equal ("text/javascript")
      __TearDown__
      Files.delete(path)
    }

    "getFileStore method should return FileStore object containing the file" in new FileFixture {
      __Exercise__
      val sut = path.fileStore
      __Verify__
      sut should equal (Files.getFileStore(path))
    }
  }
}
