package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.state.MatchState
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{IO, InputComponent, given_Monad_IO}
import it.unibo.pps.view.cli.screens.SaveMenuAction.*
import it.unibo.pps.view.i18n.{I18n, localize}

import scala.util.{Failure, Success}

enum SaveMenuAction:
  case Load, Delete

class SaveManagementScreen(
  controller: Controller,
  onMatchStart: () => Unit,
  renderMatch: (state: MatchState) => Unit,
  onScreenExit: () => IO[Unit]
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  override def render(): IO[Unit] =
    inputComponent.askForOption(
      requestKey = Some("save_menu.action_request"), 
      options = Seq("save_menu.load_action", "save_menu.delete_action").localize, 
      handleSelectedOption = handleSelectedSaveMenuAction
    )

  private def handleSelectedSaveMenuAction(ordinal: Int): IO[Unit] =
    SaveMenuAction.fromOrdinal(ordinal) match
      case Load => showSaveLoadingMenu()
      case Delete => showSaveDeletionMenu()

  private def showSaveLoadingMenu(): IO[Unit] =
    inputComponent.askForOption(
      requestKey = Some("save_loading_menu.file_choice_request"),
      options = controller.saveFileNames, 
      handleSelectedOption = handleFileLoading
    )

  private def handleFileLoading(ordinal: Int): IO[Unit] =
    val fileName = controller.saveFileNames(ordinal)
    val result = controller.loadMatch(fileName)
    result match
      case Success(state) =>
        for
          _ <- write(i18n.t("save_loading_menu.loading_success"))
          _ <- IO(() => onMatchStart())
          _ <- IO(() => renderMatch(state))
        yield ()
      case Failure(error) =>
        for
          _ <- write(i18n.t("save_loading_menu.loading_failure") :+ s"\n${error.getMessage}")
          _ <- onScreenExit()
        yield ()

  private def showSaveDeletionMenu(): IO[Unit] =
    for
      _ <- inputComponent.askForOption(
        requestKey = Some("save_deletion_menu.file_choice_request"), 
        options = controller.saveFileNames, 
        handleSelectedOption = handleFileDeletion
      )
      _ <- onScreenExit()
    yield ()

  private def handleFileDeletion(ordinal: Int): IO[Unit] =
    val fileName = controller.saveFileNames(ordinal)
    val result = controller.deleteSaveFile(fileName)
    result match
      case Success(_) => write(i18n.t("save_deletion_menu.loading_success"))
      case Failure(error) => write(i18n.t("save_deletion_menu.loading_failure") :+ s"\n${error.getMessage}")
