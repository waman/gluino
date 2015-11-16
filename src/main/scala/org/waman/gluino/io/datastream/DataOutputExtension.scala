package org.waman.gluino.io.datastream

import java.io.DataOutput

trait DataOutputExtension[T <: DataOutputExtension[T]]{ self: T =>
  
  protected def getDataOutput: DataOutput

  // 8-bit
  def <+(value: Boolean): T = {
    getDataOutput.writeBoolean(value)
    this
  }

  def <+(value: Byte): T = {
    getDataOutput.writeByte(value)
    this
  }

  def <+(value: Array[Byte]): T = {
    getDataOutput.write(value)
    this
  }

  def <+(value: (Array[Byte], Int, Int)): T = {
    getDataOutput.write(value._1, value._2, value._3)
    this
  }

  def <+(value: String): T = {
    getDataOutput.writeBytes(value)
    this
  }

  // 16-bit
  def <++(value: Int): T = {
    getDataOutput.writeShort(value)
    this
  }

  def <++(value: Array[Char]): T = {
    getDataOutput.writeChars(String.valueOf(value))
    this
  }

  def <++(value: Array[Char], start: Int, length: Int): T = {
    getDataOutput.writeChars(String.valueOf(value, start, length))
    this
  }

  def <++(value: String): T = {
    getDataOutput.writeChars(value)
    this
  }

  // 32-bit
  def <#(value: Int): T = {
    getDataOutput.writeInt(value)
    this
  }

  def <#(value: Float): T = {
    getDataOutput.writeFloat(value)
    this
  }

  // 64-bit
  def <##(value: Long): T = {
    getDataOutput.writeLong(value)
    this
  }

  def <##(value: Double): T = {
    getDataOutput.writeDouble(value)
    this
  }

  //***** << *****
  def <<(value: Boolean): T = {
    getDataOutput.writeBoolean(value)
    this
  }

  def <<(value: Byte): T = {
    getDataOutput.writeByte(value)
    this
  }

  def <<(value: Array[Byte]): T = {
    getDataOutput.write(value)
    this
  }

  def <<(value: Array[Byte], start: Int, length: Int): T = {
    getDataOutput.write(value, start, length)
    this
  }

  def <<(value: Char): T = {
    getDataOutput.writeChar(value)
    this
  }

  def <<(value: Short): T = {
    getDataOutput.writeShort(value)
    this
  }

  def <<(value: Int): T = {
    getDataOutput.writeInt(value)
    this
  }

  def <<(value: Long): T = {
    getDataOutput.writeLong(value)
    this
  }

  def <<(value: Float): T = {
    getDataOutput.writeFloat(value)
    this
  }

  def <<(value: Double): T = {
    getDataOutput.writeDouble(value)
    this
  }

  def <<(value: String): T = {
    getDataOutput.writeUTF(value)
    this
  }
}
