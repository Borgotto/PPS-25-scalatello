package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.view.cli.io.{IO, InputComponent, given_Monad_IO}
import it.unibo.pps.view.i18n.I18n

/** Implements the CLI screen that prompts the user to confirm its will to leave a match. 
 *
 * @param controller the controller of the application.
 * @param onMatchExit injected actions to perform when leaving the match.
 * @param i18n the [[i18n.I18n]] provider of the application.
 * @param inputComponent the [[io.InputComponent]] instanced for the application.
 */
class MatchQuitScreen(
  controller: Controller,
  onMatchExit: () => IO[Unit]
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  override def render(): IO[Unit] =
    for
      quitMatch <- inputComponent.askForConfirmation(
        requestKey = "match_quit_screen.request",
        invalidInputMessageKey = "generic.invalid_confirmation_choice"
      )
      _ <- if quitMatch then onMatchExit() else inputComponent.pass
    yield ()
