package it.unibo.pps.view.cli.screens

import it.unibo.pps.controller.Controller
import it.unibo.pps.domain.ActivePlayer.*
import it.unibo.pps.domain.MatchStatus.*
import it.unibo.pps.domain.Position
import it.unibo.pps.state.{BoardState, MatchState}
import it.unibo.pps.view.cli.io.BoardRenderingExtensions.render
import it.unibo.pps.view.cli.io.IO.write
import it.unibo.pps.view.cli.io.{InputComponent, IO, given_Monad_IO}
import it.unibo.pps.view.i18n.I18n

class MatchScreen(
  controller: Controller,
  state: MatchState,
  onSaveTrigger: () => IO[Unit],
  onMatchExit: () => IO[Unit]
)(using i18n: I18n, inputComponent: InputComponent) extends CLIScreen:

  private given onSaveInterrupt: IO[Unit] = onSaveTrigger()

  override def render(): IO[Unit] =
    println(state.board.render())
    handleState(state)

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
      row <- inputComponent.askForValidInput(
        requestKey = "match.placement_request_row",
        isInputValid = isValidRowForPlacement(state),
        convert = _.toInt,
        invalidInputMessageKey = "match.invalid_row"
      )
      column <- inputComponent.askForValidInput(
        requestKey = "match.placement_request_column",
        isInputValid = isValidColumnForPlacement(state, row),
        convert = _.toInt,
        invalidInputMessageKey = "match.invalid_column"
      )
      position <- IO(() => Position(row, column))
    yield position

  private def isValidRowForPlacement(state: BoardState)(s: String): Boolean =
    inputComponent.isConvertibleToInt(s)
      && (0 to state.shape.maxRow).contains(s.toInt)
      && isRowAmongAvailablePlacements(state.userAvailablePlacements)(s.toInt)

  private def isValidColumnForPlacement(state: BoardState, selectedRow: Int)(s: String): Boolean =
    inputComponent.isConvertibleToInt(s)
      && (0 to state.shape.maxColumn).contains(s.toInt)
      && state.userAvailablePlacements.contains(Position(selectedRow, s.toInt))

  private def isRowAmongAvailablePlacements(availablePlacements: Set[Position])(row: Int): Boolean =
    availablePlacements.map(position => position.row).contains(row)

  private def onMatchEnd(matchResultMessageKey: String): IO[Unit] =
    for
      _ <- write(i18n.t(matchResultMessageKey))
      _ <- onMatchExit()
    yield ()
    