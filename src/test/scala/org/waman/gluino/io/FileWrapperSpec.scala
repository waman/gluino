package org.waman.gluino.io

import java.io.File
import java.nio.file.Path

class FileWrapperSpec
    extends ReaderWrapperLikeSpec[FileWrapper]
    with InputStreamWrapperLikeSpec[FileWrapper]
    with WriterWrapperLikeSpec[FileWrapper]
    with GluinoFile{

  trait FileOperationFixture{
    val file: File = new File("path/to/some/dir")
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

  override def newReaderWrapperLike: FileWrapper = new FileWrapper(readOnlyFile)

  override def newInputStreamWrapperLike: FileWrapper = new FileWrapper(readOnlyFile)
  override def newInputStreamWrapperLike_ISO2022: FileWrapper = new FileWrapper(readOnlyFileISO2022)

  override def newWriterWrapperLike(dest: Path): FileWrapper = new FileWrapper(dest.toFile)
}
