package it.unibo.pps.model

import it.unibo.pps.model.board.Board
import it.unibo.pps.model.player.{Player, User, Opponent}
import it.unibo.pps.model.player.Opponent.RandomOpponent
import it.unibo.pps.state.{BoardState, MatchState}
import it.unibo.pps.utils.{Color, MatchStatus, Position, Shape}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.MatchStatus.*

trait Logic:
  def state: MatchState
  def placeUserDisk(position: Position): Logic
  def placeOpponentDisk(): Logic

class LogicImpl(
  private val status: MatchStatus,
  private val activePlayer: Player,
  private val board: Board
) extends Logic:

  private val boardState: BoardState = activePlayer match
    case User(color) => board.state.copy(userAvailablePlacements = board.getAvailablePlacements(color))
    case Opponent(_) => board.state

  val state: MatchState = MatchState(status, activePlayer.state, boardState)

  private val userColor: Color = activePlayer match
    case User(color) => color
    case Opponent(color) => color.opposite

  private val opponentColor: Color = userColor.opposite

  private def areBothPlayersStuck(board: Board): Boolean =
    board.getAvailablePlacements(userColor).isEmpty
    && board.getAvailablePlacements(opponentColor).isEmpty

  private def getUpdatedBoard(newDiskColor: Color, newDiskPosition: Position): Board =
    board.placeDisk(newDiskColor, newDiskPosition).captureDisks(newDiskPosition)

  private def getUpdatedStatus(board: Board): MatchStatus =
    if !areBothPlayersStuck(board) then InProgress
    else
      val userDisks = board.state.disks.count(disk => disk.color == userColor)
      val opponentDisks = board.state.disks.count(disk => disk.color == opponentColor)
      (userDisks, opponentDisks) match
        case (a, b) if a > b => UserWon
        case (a, b) if a < b => OpponentWon
        case _ => Tie

  private val nextPlayer: Player = activePlayer match
    case User(color) => RandomOpponent(color.opposite)
    case Opponent(color) => User(color.opposite)

  private def getUpdatedActivePlayer(board: Board): Player =
    val availablePlacements = board.getAvailablePlacements(activePlayer.color.opposite)
    if availablePlacements.isEmpty then activePlayer else nextPlayer

  private def getUpdatedLogic(newDiskColor: Color, newDiskPosition: Position): Logic =
    val updatedBoard = getUpdatedBoard(newDiskColor, newDiskPosition)
    val updatedStatus = getUpdatedStatus(updatedBoard)
    val updatedActivePlayer = getUpdatedActivePlayer(updatedBoard)
    new LogicImpl(updatedStatus, updatedActivePlayer, updatedBoard)

  def placeUserDisk(position: Position): Logic = activePlayer match
    case Opponent(_) => throw IllegalStateException("It is opponent's turn now")
    case user: User =>
      if !board.isPlacementValid(userColor, position) then this
      else getUpdatedLogic(userColor, position)

  def placeOpponentDisk(): Logic = activePlayer match
    case User(_) => throw IllegalStateException("It is user's turn now")
    case opponent: Opponent =>
      val position = opponent.strategy.computePlacement(using board)
      getUpdatedLogic(opponentColor, position)

object LogicImpl:

  private def getInitialActivePlayer(userColor: Color): Player = userColor match
    case Black => User(Black)
    case White => RandomOpponent(Black)

  def apply(boardShape: Shape, userColor: Color): LogicImpl =
    new LogicImpl(InProgress, getInitialActivePlayer(userColor), Board(boardShape))

  def apply(userColor: Color, board: Board) =
    new LogicImpl(InProgress, getInitialActivePlayer(userColor), board)
