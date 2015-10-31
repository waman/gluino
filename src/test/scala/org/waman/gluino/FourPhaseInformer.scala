package org.waman.gluino

import org.scalatest.Informing

trait FourPhaseInformer extends Informing{

  def __SetUp__ = null
  def __Exercise__ = null
  def __Verify__ = null
  def __TearDown__ = null

  def __SetUp__(message: String)    = { info("[set up]    " + message); null }
  def __Exercise__(message: String) = { info("[exercise]  " + message); null }
  def __Verify__(message: String)   = { info("[verify]    " + message); null }
  def __TearDown__(message: String) = { info("[tear down] " + message); null }
}
