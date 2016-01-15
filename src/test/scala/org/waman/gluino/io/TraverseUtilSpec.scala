package org.waman.gluino.io

import scala.collection.mutable
import org.waman.gluino.nio.GluinoPath._
import org.waman.gluino.io.TraverseUtil._

class TraverseUtilSpec extends GluinoIOCustomSpec{

  "convertAnyToFileVisitResult() method should" - {

    "convert any object to FileVisitResult.CONTINUE to be able to omit return values of some functions" in
      new DirectoryWithFilesFixture {
        __SetUp__
        val result = mutable.MutableList[String]()
        __Exercise__
        dir.traverse(fileType = FileType.Directories, preDir = f => result += "pre-"+f.fileName){ f =>
          result += f.fileName
        }
        __Verify__
        result should contain theSameElementsInOrderAs
          Seq("pre-dir1", "dir1", "pre-dir2", "dir2", "pre-dir3", "dir3", "pre-dir31", "dir31")
      }
  }
}
