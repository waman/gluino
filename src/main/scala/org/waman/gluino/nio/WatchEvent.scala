package org.waman.gluino.nio

import java.nio.{file => nio}
import nio.Path
import scala.language.existentials

class WatchEvent(event: nio.WatchEvent[_]){

  def context: Option[Path] = event.context() match {
    case null => Option.empty
    case path => Option.apply(path.asInstanceOf[Path])
  }

  def count: Int = event.count()

  def kind: WatchEventKind = WatchEventKind.getKindValue(event.kind())
}

sealed case class WatchEventKind(kind: nio.WatchEvent.Kind[_]){
  val name: String = kind.name()
}

object Overflow extends WatchEventKind(nio.StandardWatchEventKinds.OVERFLOW)
object EntryCreate extends WatchEventKind(nio.StandardWatchEventKinds.ENTRY_CREATE)
object EntryModify extends WatchEventKind(nio.StandardWatchEventKinds.ENTRY_MODIFY)
object EntryDelete extends WatchEventKind(nio.StandardWatchEventKinds.ENTRY_DELETE)
  
object WatchEventKind{
  def getKindValue(kind: nio.WatchEvent.Kind[_]): WatchEventKind = kind match {
    case nio.StandardWatchEventKinds.OVERFLOW => Overflow
    case nio.StandardWatchEventKinds.ENTRY_CREATE => EntryCreate
    case nio.StandardWatchEventKinds.ENTRY_MODIFY => EntryModify
    case nio.StandardWatchEventKinds.ENTRY_DELETE => EntryDelete
  }
}


class WatchEventModifier(val modifier: nio.WatchEvent.Modifier){
  val name: String = modifier.name()
}