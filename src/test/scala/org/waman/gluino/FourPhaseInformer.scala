package org.waman.gluino

import org.scalatest.Informing

trait FourPhaseInformer extends Informing{

  def __SetUp__ = null
  def __Exercise__ = null
  def __Verify__ = null
  def __TearDown__ = null

  def ___SetUp___(message: String)    = { info("[set up]    " + message); null }
  def ___Exercise___(message: String) = { info("[exercise]  " + message); null }
  def ___Verify___(message: String)   = { info("[verify]    " + message); null }
  def ___TearDown___(message: String) = { info("[tear down] " + message); null }
}
