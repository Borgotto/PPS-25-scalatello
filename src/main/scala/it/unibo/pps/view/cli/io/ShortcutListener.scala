package it.unibo.pps.view.cli.io

import org.jline.reader.{LineReader, Reference, Widget}

class SaveInterruptException extends RuntimeException("Save shortcut triggered")

trait ShortcutListener:

  private val ctrlS = "\u0013"

  protected def reader: LineReader

  private def registerShortcut(charSequence: String, name: String)(widget: Widget): Unit =
    reader.getWidgets.put(name, widget)
    reader.getKeyMaps.get(LineReader.MAIN).bind(new Reference(name), charSequence)

  private def unregisterShortcut(charSequence: String, name: String): Unit =
    reader.getWidgets.remove(name)
    reader.getKeyMaps.get(LineReader.MAIN).bind(new Reference("self-insert"), charSequence)

  protected def enableSaveShortcut(): Unit =
    val widget: Widget = () => throw new SaveInterruptException()
    registerShortcut(ctrlS, "save-shortcut")(widget)

  protected def disableSaveShortcut(): Unit =
    unregisterShortcut(ctrlS, "save-shortcut")
