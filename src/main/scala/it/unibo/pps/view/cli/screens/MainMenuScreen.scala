package it.unibo.pps.view.cli.screens

import it.unibo.pps.view.cli.io.{IO, InputComponent, given_Monad_IO}
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.i18n.{I18n, localize}

/** Implements the main menu of the application on CLI.
 * 
 * @param onNewGameAction the behavior in case the option to start a new game is selected.
 * @param onSaveManagementAction the behavior in case the save management option is selected.
 * @param i18n the [[I18n]] provider of the application.
 * @param inputComponent the [[InputComponent]] instanced for the application.
 */
class MainMenuScreen(
  onNewGameAction: () => IO[Unit],
  onSaveManagementAction: () => IO[Unit]
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  private enum MainMenuAction:
    case NewGame, SaveFiles

  override def render(): IO[Unit] =
    for
      _ <- write(i18n.t("main_menu.title"))
      _ <- askForActionSelection()
    yield ()

  private def askForActionSelection(): IO[Unit] =
    inputComponent.askForOption(
      options = Seq(
        "main_menu.actions.new_game",
        "main_menu.actions.load_saved_game",
      ).localize, 
      extraMessageKey = Some("main_menu.exit_shortcut"),
      handleSelectedOption = handleSelectedAction
    )

  private def handleSelectedAction(ordinal: Int): IO[Unit] = 
    MainMenuAction.fromOrdinal(ordinal) match
      case MainMenuAction.NewGame => onNewGameAction()
      case MainMenuAction.SaveFiles => onSaveManagementAction()
