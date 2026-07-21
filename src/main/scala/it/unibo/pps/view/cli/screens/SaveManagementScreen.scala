package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.state.MatchState
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{InputComponent, IO, given_Monad_IO}
import it.unibo.pps.view.i18n.I18n

import scala.util.{Failure, Success}

enum SaveMenuOption(val code: String):
  case Load extends SaveMenuOption("1")
  case Delete extends SaveMenuOption("2")

class SaveManagementScreen(
  controller: Controller,
  renderMatch: (state: MatchState) => Unit,
  onScreenExit: () => IO[Unit]
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  override def render(): IO[Unit] =
    val options = Seq(
      i18n.t("save_menu.load_action"),
      i18n.t("save_menu.delete_action")
    )
    for
      _ <- write(i18n.t("save_menu.action_request"))
      _ <- inputComponent.displayOptions(options)
      option <- inputComponent.askForValidInput(
        i18n.t("generic.choice_request"),
        inputComponent.isValidOptionChoice(options.size),
        identity,
        i18n.t("generic.invalid_choice")
      )
      _ <- handleSelectedSaveMenuOption(option)
    yield ()

  private def handleSelectedSaveMenuOption(option: String): IO[Unit] = option match
    case SaveMenuOption.Load.code => showSaveLoadingMenu()
    case SaveMenuOption.Delete.code => showSaveDeletionMenu()

  private def saveFilesCount: Int = controller.saveFileNames.size

  private def showSaveLoadingMenu(): IO[Unit] =
    for
      _ <- write(i18n.t("save_loading_menu.file_choice_request"))
      _ <- inputComponent.displayOptions(controller.saveFileNames)
      position <- inputComponent.askForValidInput(
        i18n.t("generic.choice_request"),
        inputComponent.isValidOptionChoice(saveFilesCount),
        _.toInt,
        i18n.t("generic.invalid_choice")
      )
      _ <- handleFileLoading(position)
    yield ()

  private def handleFileLoading(position: Int): IO[Unit] =
    val fileName = controller.saveFileNames(position - 1)
    val result = controller.loadMatch(fileName)
    result match
      case Success(state: MatchState) =>
        for
          _ <- write(i18n.t("save_loading_menu.loading_success"))
          _ <- IO(() => renderMatch(state))
        yield ()
      case Failure(_) =>
        for
          _ <- write(i18n.t("save_loading_menu.loading_failure"))
          _ <- onScreenExit()
        yield ()

  private def showSaveDeletionMenu(): IO[Unit] =
    for
      _ <- write(i18n.t("save_deletion_menu.file_choice_request"))
      _ <- inputComponent.displayOptions(controller.saveFileNames)
      position <- inputComponent.askForValidInput(
        i18n.t("generic.choice_request"),
        inputComponent.isValidOptionChoice(saveFilesCount),
        _.toInt,
        i18n.t("generic.invalid_choice")
      )
      _ <- handleFileDeletion(position)
      _ <- onScreenExit()
    yield ()

  private def handleFileDeletion(position: Int): IO[Unit] =
    val fileName = controller.saveFileNames(position - 1)
    val result = controller.deleteSaveFile(fileName)
    result match
      case Success(_) => write(i18n.t("save_deletion_menu.loading_success"))
      case Failure(_) => write(i18n.t("save_deletion_menu.loading_failure"))
