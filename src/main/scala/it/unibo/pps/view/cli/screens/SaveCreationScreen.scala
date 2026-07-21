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
      input <- inputComponent.askForValidInput(
        requestKey = "save_creation_menu.save_request",
        isInputValid = s => s.toLowerCase() == "y" || s.toLowerCase() == "n",
        invalidInputMessageKey = "save_creation_menu.invalid_choice"
      )
      saveMatch <- IO(() => input.toLowerCase() == "y")
      _ <- if saveMatch then handleSave() else IO(() => ())
    yield ()

  private def handleSave(): IO[Unit] =
    for
      _ <- write(i18n.t("save_creation_menu.filename_request"))
      filename <- inputComponent.read()
      sanitizedFilename <- IO(() => sanitize(filename))
      result <- IO(() => controller.saveMatch(sanitizedFilename))
      _ <- result match
        case Success(_) => write(i18n.t("save_creation_menu.save_success"))
        case Failure(exception) => write(i18n.t("save_creation_menu.save_failure") :+ exception.getMessage)
      _ <- IO(() => ())
    yield ()
    