package it.unibo.pps.view.cli.io

import org.jline.reader.{LineReader, Reference, Widget}

/** The exception to raise when the press of the exit shortcut is detected. */
class ExitInterruptException extends RuntimeException("Exit shortcut triggered")

/** The exception to raise when the press of the save shortcut is detected. */
class SaveInterruptException extends RuntimeException("Save shortcut triggered")

/** Handles the enabling and the disabling of keyboard shortcuts.
 * 
 * @param reader the [[LineReader]] instance that reads from the terminal.
 */
class ShortcutManager(private val reader: LineReader):

  private val ctrlC = "\u0003"
  private val ctrlS = "\u0013"

  private def registerShortcut(charSequence: String, name: String)(widget: Widget): Unit =
    reader.getWidgets.put(name, widget)
    reader.getKeyMaps.get(LineReader.MAIN).bind(Reference(name), charSequence)

  private def unregisterShortcut(charSequence: String, name: String): Unit =
    reader.getWidgets.remove(name)
    reader.getKeyMaps.get(LineReader.MAIN).unbind(charSequence)
  
  /** Enables the shortcut to exit the application. */
  def enableExitShortcut(): Unit =
    val widget: Widget = () => throw new ExitInterruptException()
    registerShortcut(ctrlC, "exit-shortcut")(widget)

  /** Enables the shortcut to save a match. */
  def enableSaveShortcut(): Unit =
    val widget: Widget = () => throw new SaveInterruptException()
    registerShortcut(ctrlS, "save-shortcut")(widget)

  /** Disables the shortcut to save a match. */
  def disableSaveShortcut(): Unit = unregisterShortcut(ctrlS, "save-shortcut")
