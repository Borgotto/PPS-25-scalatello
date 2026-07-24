package it.unibo.pps.view.cli.io

import org.jline.reader.{LineReader, Reference, Widget}

class ExitInterruptException extends RuntimeException("Exit shortcut triggered")
class SaveInterruptException extends RuntimeException("Save shortcut triggered")

class ShortcutManager(private val reader: LineReader):

  private val ctrlC = "\u0003"
  private val ctrlS = "\u0013"

  private def registerShortcut(charSequence: String, name: String)(widget: Widget): Unit =
    reader.getWidgets.put(name, widget)
    reader.getKeyMaps.get(LineReader.MAIN).bind(Reference(name), charSequence)

  private def unregisterShortcut(charSequence: String, name: String): Unit =
    reader.getWidgets.remove(name)
    reader.getKeyMaps.get(LineReader.MAIN).unbind(charSequence)
  
  def enableExitShortcut(): Unit =
    val widget: Widget = () => throw new ExitInterruptException()
    registerShortcut(ctrlC, "exit-shortcut")(widget)

  def enableSaveShortcut(): Unit =
    val widget: Widget = () => throw new SaveInterruptException()
    registerShortcut(ctrlS, "save-shortcut")(widget)

  def disableSaveShortcut(): Unit = unregisterShortcut(ctrlS, "save-shortcut")
