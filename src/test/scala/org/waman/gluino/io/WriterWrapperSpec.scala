package org.waman.gluino.io

import java.io.{BufferedWriter, Writer}
import java.nio.file.{Path, Files}

import org.waman.gluino.GluinoIOCustomSpec

class WriterWrapperSpec extends GluinoIOCustomSpec with AppendableConverter{

  "***** Factory method *****" - {

    "the Writer is retained directly by wrapper if an instance of BufferedWriter" in {
      __SetUp__
      val writer = new BufferedWriter(mock[Writer])
      __Exercise__
      val wrapper: WriterWrapper = WriterWrapper(writer)
      __Verify__
      wrapper.writer should be (a [BufferedWriter])
      wrapper.writer should be theSameInstanceAs writer
    }

    "the Writer is wrapped by BufferedWriter and then retained if not an instance of BufferedWriter" in {
      __SetUp__
      val writer = mock[Writer]
      __Exercise__
      val wrapper = WriterWrapper(writer)
      __Verify__
      wrapper.writer should be (a [BufferedWriter])
      wrapper.writer should not be theSameInstanceAs (writer)
    }
  }

  "withWriter() method should" - {

    "flush and close writer after use" in {
      __SetUp__
      val writer = mock[Writer]
      (writer.flush _).expects()
      (writer.close _).expects()
      __Exercise__
      writer.withWriter{ _ => }
      __Verify__
    }
  }

  "writeLine() method should" - {

    "not close the writer after use" in new WriterFixture {
      __Exercise__
      writer.writeLine("first line.")
      __Verify__
      writer should be (opened)
    }

    "write down the specified line to the writer" in new WriterWithContentFixture {
      __Exercise__
      writer.writeLine("fourth line.")
      writer.flush()
      writer.close()
      __Verify__
      Files.readAllLines(destPath) should contain theSameElementsInOrderAs
        (content :+ "fourth line.")
    }
  }

  "writeLines(Seq[String]) method should" - {

    "not close the writer after use" in new WriterFixture {
      __Exercise__
      writer.writeLines(Seq("first line.", "second line."))
      __Verify__
      writer should be (opened)
    }

    "write down the specified lines to the writer" in new WriterWithContentFixture {
      __Exercise__
      writer.writeLines(Seq("fourth line.", "fifth line."))
      writer.flush()
      writer.close()
      __Verify__
      Files.readAllLines(destPath) should contain theSameElementsInOrderAs
        (content :+ "fourth line.")
    }
  }

  "WriterWrapper#append() method should" - {
    // WriterWrapper#append() is not implicitly applied due to overloading

    "not close the writer after use" in new WriterFixture {
      __SetUp__
      val wrapped = wrapWriter(writer)
      __Exercise__
      wrapped.append("first line.")
      __Verify__
      writer should be (opened)
    }

    "append the specified lines to the writer" in new WriterWithContentFixture {
      __SetUp__
      val wrapped = wrapWriter(writer)
      __Exercise__
      wrapped.append("fourth line.")
      writer.flush()
      writer.close()
      __Verify__
      text(destPath) should contain theSameElementsInOrderAs
        (contentAsString + "fourth line.")
    }
  }

  "<< operator should" - {

    "not close the writer after use" in new WriterFixture {
      __Exercise__
      writer << "first line."
      __Verify__
      writer should be (opened)
    }

    "append the specified Writable to the writer" in new WriterWithContentFixture {
      __Exercise__
      writer << "fourth line."
      writer.flush()
      writer.close()
      __Verify__
      text(destPath) should contain theSameElementsInOrderAs
        (contentAsString + "fourth line.")
    }

    "sequentially append the specified Writables to the writer" in new WriterWithContentFixture {
      __Exercise__
      writer << "fourth " << "line."
      writer.flush()
      writer.close()
      __Verify__
      text(destPath) should contain theSameElementsInOrderAs
        (content :+ "fourth line.")
    }
  }

  def text(path: Path): String = new String(Files.readAllBytes(path))
}