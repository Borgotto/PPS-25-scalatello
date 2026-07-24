package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.state.MatchState
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{IO, InputComponent, given_Monad_IO}
import it.unibo.pps.view.cli.screens.SaveMenuAction.*
import it.unibo.pps.view.i18n.{I18n, localize}

import scala.util.{Failure, Success}

enum SaveMenuAction:
  case Load, Delete, GoBack

class SaveManagementScreen(
  controller: Controller,
  onMatchStart: () => Unit,
  renderMatch: (state: MatchState) => Unit,
  goBackToMainMenu: () => IO[Unit]
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  override def render(): IO[Unit] =
    inputComponent.askForOption(
      requestKey = Some("save_menu.action_request"), 
      options = Seq(
        "save_menu.actions.load",
        "save_menu.actions.delete",
        "generic.actions.go_back"
      ).localize,
      handleSelectedOption = handleSelectedSaveMenuAction
    )

  private def handleSelectedSaveMenuAction(ordinal: Int): IO[Unit] =
    SaveMenuAction.fromOrdinal(ordinal) match
      case Load => showSaveLoadingMenu()
      case Delete => showSaveDeletionMenu()
      case GoBack => goBackToMainMenu()

  private def onEmptySaveFilesList(): IO[Unit] =
    for
      _ <- write(i18n.t("save_loading_menu.no_files_message"))
      _ <- goBackToMainMenu()
    yield ()

  private def isGoBackOption(ordinal: Int, numOptions: Int): Boolean =
    ordinal == numOptions - 1

  private def goBack(): IO[Unit] = this.render()

  private def handleSelectedOption(
    numOptions: Int,
    onFileSelected: Int => IO[Unit]
  )(ordinal: Int): IO[Unit] =
    if isGoBackOption(ordinal, numOptions) then goBack()
    else onFileSelected(ordinal)

  private def showFileSelectionMenu(
    requestKey: String,
    onFileSelected: Int => IO[Unit]
  ): IO[Unit] =
    val saveFileNames = controller.saveFileNames
    if saveFileNames.isEmpty then onEmptySaveFilesList()
    else
      val options = saveFileNames :+ i18n.t("generic.actions.go_back")
      inputComponent.askForOption(
        requestKey = Some(requestKey),
        options = options,
        handleSelectedOption = handleSelectedOption(options.size, onFileSelected)
      )

  private def showSaveLoadingMenu(): IO[Unit] =
    showFileSelectionMenu(
      requestKey = "save_loading_menu.file_choice_request",
      onFileSelected = handleFileLoading
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
          _ <- goBackToMainMenu()
        yield ()

  private def showSaveDeletionMenu(): IO[Unit] =
    showFileSelectionMenu(
      requestKey = "save_deletion_menu.file_choice_request",
      onFileSelected = handleFileDeletion
    )

  private def handleFileDeletion(ordinal: Int): IO[Unit] =
    val fileName = controller.saveFileNames(ordinal)
    val result = controller.deleteSaveFile(fileName)
    val resultMessage = result match
      case Success(_) => i18n.t("save_deletion_menu.loading_success")
      case Failure(error) => i18n.t("save_deletion_menu.loading_failure") :+ s"\n${error.getMessage}"
    for
      _ <- write(resultMessage)
      _ <- goBackToMainMenu()
    yield ()
