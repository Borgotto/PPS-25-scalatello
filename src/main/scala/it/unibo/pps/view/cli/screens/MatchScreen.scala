package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.domain.ActivePlayer.*
import it.unibo.pps.domain.MatchStatus.*
import it.unibo.pps.domain.Position
import it.unibo.pps.state.{BoardState, MatchState}
import it.unibo.pps.view.cli.io.BoardRendering.render
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{InputComponent, IO, given_Monad_IO}
import it.unibo.pps.view.i18n.I18n

/** Implements the CLI screen that shows the current state of the match.
 *
 * @param controller the controller of the application.
 * @param matchState the current state of the match.
 * @param onSaveTrigger the behavior in case the shortcut to save the match is triggered.
 * @param onMatchExit injected actions to perform when leaving the match.
 * @param i18n the [[I18n]] provider of the application.
 * @param inputComponent the [[InputComponent]] instanced for the application.
 */
class MatchScreen(
  controller: Controller,
  matchState: MatchState,
  onSaveTrigger: () => IO[Unit],
  onMatchExit: () => IO[Unit]
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  private given onSaveInterrupt: IO[Unit] = onSaveTrigger()

  override def render(): IO[Unit] =
    for
      _ <- write(matchState.board.render())
      _ <- handleState(matchState)
    yield ()

  private def handleState(state: MatchState): IO[Unit] =
    state.status match
      case InProgress => state.activePlayer match
        case User => onUserTurn(state.board)
        case Opponent => onOpponentTurn()
      case UserWon => onMatchEnd("match.result.user_won")
      case OpponentWon => onMatchEnd("match.result.opponent_won")
      case Tie => onMatchEnd("match.result.tie")

  private def onOpponentTurn(): IO[Unit] = write(i18n.t("match.opponent_turn_message"))

  private def onUserTurn(state: BoardState): IO[Unit] =
    for
      _ <- write(i18n.t("match.user_turn_message"))
      position <- askForValidPlacement(state)
      _ <- IO(() => controller.handleSelection(position))
    yield ()

  private def askForValidPlacement(state: BoardState): IO[Position] =
    for
      row <- inputComponent.askForInteger(
        requestKey = "match.placement_request_row",
        isNumberValid = isValidRowForPlacement(state),
        invalidInputMessageKey = "match.invalid_row"
      )
      column <- inputComponent.askForInteger(
        requestKey = "match.placement_request_column",
        isNumberValid = isValidColumnForPlacement(state, row),
        invalidInputMessageKey = "match.invalid_column"
      )
      position <- IO(() => Position(row, column))
    yield position

  private def isValidRowForPlacement(state: BoardState)(row: Int): Boolean =
      (0 to state.shape.maxRowIndex).contains(row)
      && state.userAvailablePlacements.map(_.row).contains(row)

  private def isValidColumnForPlacement(state: BoardState, row: Int)(column: Int): Boolean =
      (0 to state.shape.maxColumnIndex).contains(column)
      && state.userAvailablePlacements.contains(Position(row, column))

  private def onMatchEnd(matchResultMessageKey: String): IO[Unit] =
    for
      _ <- write(i18n.t(matchResultMessageKey))
      _ <- onMatchExit()
    yield ()
    