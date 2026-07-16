package it.unibo.pps.view.io

import org.jline.reader.{LineReader, Reference, Widget}
import org.jline.terminal.Terminal.Signal

class SaveInterruptException extends RuntimeException("Save shortcut triggered")

trait ShortcutListener:

  private val ctrlS = "\u0013"

  protected def reader: LineReader

  private def registerShortcut(charSequence: String, name: String)(widget: Widget)(action: => Unit): Unit =
    reader.getWidgets.put(name, widget)
    reader.getKeyMaps.get(LineReader.MAIN).bind(new Reference(name), charSequence)

  private def unregisterShortcut(charSequence: String, name: String): Unit =
    reader.getWidgets.remove(name)
    reader.getKeyMaps.get(LineReader.MAIN).bind(new Reference("self-insert"), charSequence)

  protected def enableSaveShortcut(action: => Unit): Unit =
    val widget: Widget = () => throw new SaveInterruptException()
    registerShortcut(ctrlS, "save-shortcut")(widget)(action)

  protected def disableSaveShortcut(): Unit =
    unregisterShortcut(ctrlS, "save-shortcut")
