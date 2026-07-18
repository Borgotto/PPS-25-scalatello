package it.unibo.pps.model

import it.unibo.pps.domain.{ActivePlayer, Color, MatchStatus, OpponentType, Position, Shape}
import it.unibo.pps.model.board.Board
import it.unibo.pps.model.player.{Opponent, User}
import it.unibo.pps.model.player.Opponent.{EasyOpponent, HardOpponent, MediumOpponent, RandomOpponent, SmartOpponent}
import it.unibo.pps.state.{BoardState, MatchState}
import it.unibo.pps.domain.Color.*
import it.unibo.pps.domain.MatchStatus.*
import it.unibo.pps.domain.OpponentType.*

trait Logic:
  def state: MatchState
  def placeUserDisk(position: Position): Logic
  def placeOpponentDisk(): Logic

class StandardLogic(
  private val status: MatchStatus,
  private val user: User,
  private val opponent: Opponent,
  private val activePlayer: ActivePlayer,
  private val board: Board
) extends Logic:

  private val boardState: BoardState = activePlayer match
    case ActivePlayer.User => board.state.copy(userAvailablePlacements = board.getAvailablePlacements(user.color))
    case ActivePlayer.Opponent => board.state

  val state: MatchState = MatchState(status, user, opponent, activePlayer, boardState)

  private def areBothPlayersStuck(board: Board): Boolean =
    board.getAvailablePlacements(user.color).isEmpty
    && board.getAvailablePlacements(opponent.color).isEmpty

  private def getUpdatedBoard(newDiskColor: Color, newDiskPosition: Position): Board =
    board.placeDisk(newDiskColor, newDiskPosition)

  private def getUpdatedStatus(board: Board): MatchStatus =
    if !areBothPlayersStuck(board) then InProgress
    else
      val userDisks = board.state.disks.count(disk => disk.color == user.color)
      val opponentDisks = board.state.disks.count(disk => disk.color == opponent.color)
      (userDisks, opponentDisks) match
        case (a, b) if a > b => UserWon
        case (a, b) if a < b => OpponentWon
        case _ => Tie

  private val nextPlayer: ActivePlayer = activePlayer match
    case ActivePlayer.User => ActivePlayer.Opponent
    case ActivePlayer.Opponent => ActivePlayer.User

  private def activePlayerColor: Color = activePlayer match
    case ActivePlayer.User => user.color
    case ActivePlayer.Opponent => opponent.color
  
  private def getUpdatedActivePlayer(board: Board): ActivePlayer =
    val availablePlacements = board.getAvailablePlacements(activePlayerColor.opposite)
    if availablePlacements.isEmpty then activePlayer else nextPlayer

  private def getUpdatedLogic(newDiskColor: Color, newDiskPosition: Position): Logic =
    val updatedBoard = getUpdatedBoard(newDiskColor, newDiskPosition)
    val updatedStatus = getUpdatedStatus(updatedBoard)
    val updatedActivePlayer = getUpdatedActivePlayer(updatedBoard)
    new StandardLogic(updatedStatus, user, opponent, updatedActivePlayer, updatedBoard)

  def placeUserDisk(position: Position): Logic = activePlayer match
    case ActivePlayer.Opponent => throw IllegalStateException("It is opponent's turn now")
    case ActivePlayer.User =>
      if !board.isPlacementValid(user.color, position) then this
      else getUpdatedLogic(user.color, position)

  def placeOpponentDisk(): Logic = activePlayer match
    case ActivePlayer.User => throw IllegalStateException("It is user's turn now")
    case ActivePlayer.Opponent =>
      val position = opponent.strategy.computePlacement(using board)
      getUpdatedLogic(opponent.color, position)

object Logic:
  
  private def getUser(color: Color): User = User(color)
  
  private def getOpponent(color: Color, opponentType: OpponentType): Opponent =
    opponentType match
      case Random => RandomOpponent(color)
      case Easy => EasyOpponent(color)
      case Medium => MediumOpponent(color)
      case Hard => HardOpponent(color)

  private def getInitialActivePlayer(userColor: Color): ActivePlayer = userColor match
    case Black => ActivePlayer.User
    case White => ActivePlayer.Opponent

  def apply(boardShape: Shape, userColor: Color, opponentType: OpponentType): StandardLogic =
    new StandardLogic(
      InProgress, 
      getUser(userColor),
      getOpponent(userColor.opposite, opponentType),
      getInitialActivePlayer(userColor),
      Board(boardShape)
    )

  def apply(userColor: Color, opponentType: OpponentType, board: Board) =
    new StandardLogic(
      InProgress,
      getUser(userColor),
      getOpponent(userColor.opposite, opponentType),
      getInitialActivePlayer(userColor),
      board
    )

  def apply(state: MatchState): StandardLogic =
    val board = Board(state.board)
    new StandardLogic(state.status, state.user, state.opponent, state.activePlayer, board)
