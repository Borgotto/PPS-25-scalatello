package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{InputComponent, IO, given_Monad_IO}
import it.unibo.pps.view.i18n.I18n

import scala.util.{Failure, Success}

/** Implements the CLI screen for the creation of match save.
 * 
 * @param controller the controller of the application.
 * @param i18n the [[i18n.I18n]] provider of the application.
 * @param inputComponent the [[io.InputComponent]] instanced for the application.
 */
class SaveCreationScreen(
  controller: Controller,
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  override def render(): IO[Unit] =
    for
      saveMatch <- inputComponent.askForConfirmation(
        requestKey = "save_creation_menu.save_request",
        invalidInputMessageKey = "generic.invalid_confirmation_choice"
      )
      _ <- if saveMatch then handleSave() else inputComponent.pass
    yield ()

  private def handleSave(): IO[Unit] =
    for
      filename <- inputComponent.askForFilename(
        requestKey = "save_creation_menu.filename_request"
      )
      result <- IO(() => controller.saveMatch(filename))
      _ <- result match
        case Success(_) => write(i18n.t("save_creation_menu.save_success"))
        case Failure(exception) => write(i18n.t("save_creation_menu.save_failure") :+ s"\n${exception.getMessage}")
    yield ()
    