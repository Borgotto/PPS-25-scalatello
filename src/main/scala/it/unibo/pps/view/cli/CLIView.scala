package it.unibo.pps.view.cli

import it.unibo.pps.controller.Controller
import it.unibo.pps.state.MatchState
import it.unibo.pps.view.View
import it.unibo.pps.view.cli.io.{InputComponent, IO, ShortcutListener, given_Monad_IO}
import it.unibo.pps.view.cli.screens.{MainMenuScreen, MatchScreen, MatchSetupMenu, SaveCreationScreen, SaveManagementScreen}
import it.unibo.pps.view.i18n.I18n

import org.jline.reader.{LineReader, LineReaderBuilder}
import org.jline.terminal.TerminalBuilder

class CLIView(private val i18n: I18n) extends View(i18n) with ShortcutListener:

  private val controller = Controller(this)

  private val terminal = TerminalBuilder.builder().system(true).build()
  protected val reader: LineReader = LineReaderBuilder.builder().terminal(terminal).build()
  
  private val inputComponent = InputComponent(reader)
  private val mainMenuScreen = MainMenuScreen(i18n, inputComponent, showMatchSetupMenu, showSaveManagementMenu)
  private val matchSetupScreen = MatchSetupMenu(i18n, inputComponent, controller, enableSaveShortcut)
  private val saveCreationScreen = SaveCreationScreen(i18n, inputComponent, controller)
  private val saveManagementScreen = SaveManagementScreen(i18n, inputComponent, controller, update, showMainMenu)

  private var currentMatchState: MatchState = _

  override def show(): Unit = showMainMenu()

  private def showMainMenu(): IO[Unit] = mainMenuScreen.render()

  private def showMatchSetupMenu(): IO[Unit] = matchSetupScreen.render()

  private def showSaveCreationMenu(): IO[Unit] =
    for
      _ <- saveCreationScreen.render()
      _ <- IO(() => update(currentMatchState))
    yield ()

  private def onMatchEnd(): IO[Unit] =
    disableSaveShortcut()
    showMainMenu()

  override def update(state: MatchState): Unit =
    currentMatchState = state
    val matchScreen = MatchScreen(i18n, inputComponent, controller, state, showSaveCreationMenu, onMatchEnd)
    matchScreen.render()

  private def showSaveManagementMenu(): IO[Unit] = saveManagementScreen.render()
