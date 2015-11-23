package org.waman.gluino.io

trait FileType{
  def filter[F](path: F)(implicit provider: FileTypeFilterProvider[F]): Boolean =
    getFilter(provider)(path)

  def getFilter[F](provider: FileTypeFilterProvider[F]): F => Boolean
}

trait FileTypeFilterProvider[F]{
  def getFilterForFile: F => Boolean
  def getFilterForDirectory: F => Boolean
}

object FileType{
  object Files extends FileType {
    override def getFilter[F](provider: FileTypeFilterProvider[F]): F => Boolean =
      provider.getFilterForFile
  }

  object Directories extends FileType {
    override def getFilter[F](provider: FileTypeFilterProvider[F]): F => Boolean =
      provider.getFilterForDirectory
  }

  object Any extends FileType {
    override def getFilter[F](provider: FileTypeFilterProvider[F]): F => Boolean =
      path => true
  }
}