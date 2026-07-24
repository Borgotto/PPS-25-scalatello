package it.unibo.pps.view.cli

import it.unibo.pps.controller.Controller
import it.unibo.pps.state.MatchState
import it.unibo.pps.view.View
import it.unibo.pps.view.cli.io.{ExitInterruptException, given_Monad_IO, InputComponent, IO, ShortcutManager}
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.screens.*
import it.unibo.pps.view.i18n.I18n

import org.jline.reader.{LineReader, LineReaderBuilder}
import org.jline.terminal.TerminalBuilder

class CLIView(using i18n: I18n) extends View:

  private val controller = Controller()

  private val terminal = TerminalBuilder.builder().system(true).build()
  private val reader: LineReader = LineReaderBuilder.builder().terminal(terminal).build()
  sys.addShutdownHook(terminal.close())

  private val shortcutManager = ShortcutManager(reader)
  given inputComponent: InputComponent = InputComponent(reader)

  private val mainMenuScreen = MainMenuScreen(
    onNewGameAction = showMatchSetupScreen,
    onSaveManagementAction = showSaveManagementScreen
  )
  
  private val matchSetupScreen = MatchSetupScreen(controller, onMatchStart)
  
  private val saveCreationScreen = SaveCreationScreen(controller)
  
  private val saveManagementScreen = SaveManagementScreen(
    controller,
    onMatchStart,
    renderMatch = update,
    onScreenExit = goBackToMainMenu
  )

  override def show(): Unit =
    try
      shortcutManager.enableExitShortcut()
      showMainMenu()
    catch case _: ExitInterruptException => exit()

  private def showMainMenu(): IO[Unit] = mainMenuScreen.render()

  private def showMatchSetupScreen(): IO[Unit] = matchSetupScreen.render()

  private def showSaveCreationScreen(currentMatchState: MatchState): IO[Unit] =
    for
      _ <- IO(() => shortcutManager.disableSaveShortcut())
      _ <- saveCreationScreen.render()
      _ <- IO(() => shortcutManager.enableSaveShortcut())
      _ <- IO(() => update(currentMatchState))
    yield ()
  
  private def goBackToMainMenu(): IO[Unit] =
    for 
      _ <- write(i18n.t("generic.back_to_menu_message"))
      _ <- showMainMenu()
    yield ()
  
  private def onMatchStart(): Unit =
    controller.subscribe(this)
    shortcutManager.enableSaveShortcut()
  
  private def onMatchExit(): IO[Unit] =
    controller.unsubscribe(this)
    shortcutManager.disableSaveShortcut()
    goBackToMainMenu()

  override def update(state: MatchState): Unit =
    val matchScreen = MatchScreen(
      controller,
      state,
      onSaveTrigger = () => showSaveCreationScreen(state),
      onMatchExit = onMatchExit
    )
    matchScreen.render()

  private def showSaveManagementScreen(): IO[Unit] = saveManagementScreen.render()

  private def exit(): Unit =
    for
      _ <- write(i18n.t("generic.exit_shortcut_detected"))
      _ <- write(i18n.t("generic.exit_message"))
    yield ()
    terminal.close()
    sys.exit(0)
