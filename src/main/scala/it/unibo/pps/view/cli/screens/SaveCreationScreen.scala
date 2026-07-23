package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.Sanitizer.sanitize
import it.unibo.pps.view.cli.io.{InputComponent, IO, given_Monad_IO}
import it.unibo.pps.view.i18n.I18n

import scala.util.{Failure, Success}

class SaveCreationScreen(
  controller: Controller,
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  override def render(): IO[Unit] =
    for
      saveMatch <- inputComponent.askForConfirmation(
        requestKey = "save_creation_menu.save_request",
        invalidInputMessageKey = "save_creation_menu.invalid_choice"
      )
      _ <- if saveMatch then handleSave() else inputComponent.pass
    yield ()

  private def handleSave(): IO[Unit] =
    for
      sanitizedFilename <- inputComponent.askForFilename(
        requestKey = "save_creation_menu.filename_request", 
        sanitize = filename => sanitize(filename)
      )
      result <- IO(() => controller.saveMatch(sanitizedFilename))
      _ <- result match
        case Success(_) => write(i18n.t("save_creation_menu.save_success"))
        case Failure(exception) => write(i18n.t("save_creation_menu.save_failure") :+ s"\n${exception.getMessage}")
    yield ()
    