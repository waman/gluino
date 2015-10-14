package org.waman.gluino.nio

import java.io.{InputStream, OutputStream, BufferedReader, Writer}

trait AppendableConverter {

  implicit def convertByteArrayToOutputtable(bytes: Array[Byte]): Outputtable = new Outputtable {
    override def outputTo(output: OutputStream): Unit = {
      output.write(bytes)
    }
  }

  implicit def convertInputStreamToOutputtable(input: InputStream): Outputtable = new Outputtable{
    def outputTo(output: OutputStream): Unit = try{
      var i = input.read()
      while(i != -1) {
        output.write(i)
        i = input.read()
      }
    }finally{
      input.close()
    }
  }

  implicit def convertStringToWritable(text: String): Writable = new Writable{
    override def writeTo(writer: Writer): Unit = {
      writer.write(text)
    }
  }

  implicit def convertBufferedReaderToWritable(reader: BufferedReader): Writable = new Writable{
    override def writeTo(writer: Writer): Unit = try{
      var line = reader.readLine()
      while(line != null){
        writer.write(line)
        line = reader.readLine()
      }
    }finally{
      reader.close()
    }
  }
}
