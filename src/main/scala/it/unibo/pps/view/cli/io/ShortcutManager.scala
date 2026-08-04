package it.unibo.pps.view.cli.io

import org.jline.reader.{LineReader, Reference, Widget}

/** The exception to raise when the press of the shortcut to exit the application is detected. */
class ExitAppInterruptException extends RuntimeException("App exit shortcut triggered")

/** The exception to raise when the press of the shortcut to save a match is detected. */
class SaveMatchInterruptException extends RuntimeException("Save match shortcut triggered")

/** The exception to raise when the press of the shortcut to quit a match is detected. */
class QuitMatchInterruptException extends RuntimeException("Quit match shortcut triggered")

/** Handles the enabling and the disabling of keyboard shortcuts.
 * 
 * @param reader the [[org.jline.reader.LineReader]] instance that reads from the terminal.
 */
class ShortcutManager(private val reader: LineReader):

  private val ctrlC = "\u0003"
  private val ctrlS = "\u0013"
  private val ctrlQ = "\u0011"

  private def registerShortcut(charSequence: String, name: String)(widget: Widget): Unit =
    reader.getWidgets.put(name, widget)
    reader.getKeyMaps.get(LineReader.MAIN).bind(Reference(name), charSequence)

  private def unregisterShortcut(charSequence: String, name: String): Unit =
    reader.getWidgets.remove(name)
    reader.getKeyMaps.get(LineReader.MAIN).unbind(charSequence)
  
  /** Enables the shortcut to exit the application. */
  def enableAppExitShortcut(): Unit =
    val widget: Widget = () => throw new ExitAppInterruptException()
    registerShortcut(ctrlC, "exit-app-shortcut")(widget)
  
  private def enableMatchSaveShortcut(): Unit =
    val widget: Widget = () => throw new SaveMatchInterruptException()
    registerShortcut(ctrlS, "save-match-shortcut")(widget)
  
  private def disableMatchSaveShortcut(): Unit = unregisterShortcut(ctrlS, "save-match-shortcut")
  
  private def enableMatchQuitShortcut(): Unit =
    val widget: Widget = () => throw new QuitMatchInterruptException()
    registerShortcut(ctrlQ, "quit-match-shortcut")(widget)
  
  private def disableMatchQuitShortcut(): Unit = unregisterShortcut(ctrlQ, "quit-match-shortcut")

  /** Enables all the shortcuts that are usable during a match. */
  def enableMatchShortcuts(): Unit =
    enableMatchSaveShortcut()
    enableMatchQuitShortcut()

  /** Disables all the shortcuts that are usable during a match. */
  def disableMatchShortcuts(): Unit =
    disableMatchSaveShortcut()
    disableMatchQuitShortcut()
