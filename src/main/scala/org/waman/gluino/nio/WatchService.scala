package org.waman.gluino.nio

import java.nio.{file => nio}
import java.nio.file.{Watchable, Path, FileSystems}

import scala.collection.JavaConversions._

class WatchService(watcher: nio.WatchService) {

  def register(path: Path, kinds: Set[WatchEventKind]): WatchKey =
    new WatchKey(path.register(watcher, kinds.map(_.kind).toArray:_*))

  def register(path: Path, kinds: Set[WatchEventKind], modifier: Set[WatchEventModifier]): WatchKey =
    new WatchKey(path.register(watcher, kinds.map(_.kind).toArray, modifier.map(_.modifier).toArray:_*))
}

object WatchService{
  
  def apply(): WatchService =
    new WatchService(FileSystems.getDefault.newWatchService())
}

class WatchKey(key: nio.WatchKey){

  def pollEvents: Seq[WatchEvent] = key.pollEvents().map(new WatchEvent(_))

  def reset(): Boolean = key.reset()
  def isValid: Boolean = key.isValid
  def cancel(): Unit = key.cancel()
  def watchable: Watchable = key.watchable
}