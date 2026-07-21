package it.unibo.pps.view.cli.screens

import it.unibo.pps.view.cli.io.{InputComponent, IO, given_Monad_IO}
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.i18n.I18n

enum Action(val code: String):
  case NewGame extends Action("1")
  case SaveFiles extends Action("2")
  case Quit extends Action("3")

class MainMenuScreen(
  onNewGameAction: () => IO[Unit],
  onSaveManagementAction: () => IO[Unit]
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  override def render(): IO[Unit] =
    for
      _ <- write(i18n.t("main_menu.title"))
      _ <- askForActionSelection()
    yield ()

  private def askForActionSelection(): IO[Unit] =
    val options = Seq(
      i18n.t("main_menu.actions.new_game"),
      i18n.t("main_menu.actions.load_saved_game"),
      i18n.t("main_menu.actions.quit")
    )
    for
      _ <- inputComponent.displayOptions(options)
      action <- inputComponent.askForValidInput(
        i18n.t("main_menu.action_request"),
        inputComponent.isValidOptionChoice(options.size),
        identity,
        i18n.t("generic.invalid_choice")
      )
      _ <- handleSelectedAction(action)
    yield ()

  private def handleSelectedAction(option: String): IO[Unit] = option match
    case Action.NewGame.code => onNewGameAction()
    case Action.SaveFiles.code => onSaveManagementAction()
    case Action.Quit.code => write(i18n.t("generic.exit_message"))
