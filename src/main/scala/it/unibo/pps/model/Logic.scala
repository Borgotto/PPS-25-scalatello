package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, BoardImpl}
import it.unibo.pps.model.Player
import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.{Color, MatchStatus, Position, Shape}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.MatchStatus.*

trait Logic:
  val state: MatchState
  def placeUserDisk(position: Position): Logic
  def placeOpponentDisk(): Logic

class LogicImpl(
  private val status: MatchStatus,
  private val activePlayer: Player,
  private val board: Board
) extends Logic:

  override val state = MatchState(status, activePlayer.state, board.state)

  private def userColor: Color = activePlayer match
    case User(color) => color
    case Opponent(color) => color.opposite

  private def opponentColor: Color = userColor.opposite

  private def areBothPlayersStuck: Boolean =
    board.getAvailablePlacements(userColor).isEmpty
    && board.getAvailablePlacements(opponentColor).isEmpty

  private def updatedStatus: MatchStatus =
    if !areBothPlayersStuck then InProgress
    else
      val userDisks = board.state.disks.count(disk => disk.color == userColor)
      val opponentDisks = board.state.disks.count(disk => disk.color == opponentColor)
      (userDisks, opponentDisks) match
        case (a, b) if a > b => UserWon
        case (a, b) if a < b => OpponentWon
        case _ => Tie

  private def nextPlayer: Player = activePlayer match
    case User(color) => Opponent(color.opposite)
    case Opponent(color) => User(color.opposite)

  private def updatedActivePlayer: Player =
    if board.getAvailablePlacements(activePlayer.color.opposite).isEmpty then activePlayer else nextPlayer

  override def placeUserDisk(position: Position): Logic = activePlayer match
    case Opponent(_) => throw IllegalStateException("It is opponent's turn now")
    case user: User =>
      if !board.isPlacementValid(userColor, position) then this
      else
        val updatedBoard = board.placeDisk(userColor, position).captureDisks(position)
        new LogicImpl(updatedStatus, updatedActivePlayer, updatedBoard)

  override def placeOpponentDisk(): Logic = activePlayer match
    case User(_) => throw IllegalStateException("It is user's turn now")
    case opponent: Opponent =>
      val position = opponent.strategy.computePlacement(using board.state)
      val updatedBoard = board.placeDisk(opponentColor, position).captureDisks(position)
      new LogicImpl(updatedStatus, updatedActivePlayer, updatedBoard)

object LogicImpl:
  
  private def getInitialActivePlayer(userColor: Color): Player = userColor match
    case Black => User(Black)
    case White => Opponent(Black)
    
  def apply(boardShape: Shape, userColor: Color): LogicImpl =
    new LogicImpl(InProgress, getInitialActivePlayer(userColor), BoardImpl(boardShape))
    
  def apply(userColor: Color, board: Board) =
    new LogicImpl(InProgress, getInitialActivePlayer(userColor), board)
    