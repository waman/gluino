package org.waman.gluino.io

import java.io.{BufferedWriter, Writer}
import java.nio.file.Files

import org.waman.gluino.GluinoCustomSpec

class WriterWrapperSpec extends GluinoCustomSpec with GluinoIO{

  "factory method" - {

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
      writer.withWriter{ w => }
      __Verify__
    }
  }

  trait WriterFixture extends FileFixture{
    val writer = Files.newBufferedWriter(path)
  }
}