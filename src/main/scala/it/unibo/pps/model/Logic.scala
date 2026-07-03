package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, BoardImpl}
import it.unibo.pps.state.MatchState
import it.unibo.pps.model.Player
import it.unibo.pps.utils.{Color, MatchStatus, Position, Shape}

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

  override def placeUserDisk(position: Position): Logic =
    activePlayer match
      case Opponent(_) => throw IllegalStateException("It is opponent's turn now")
      case user: User =>
        val opponentColor = activePlayer.color.opposite
        if !board.isPlacementValid(user.color, position) then this
        else
          val newBoard = board.placeDisk(user.color, position).captureDisks(position)
          val newActivePlayer = if board.getAvailablePlacements(opponentColor).isEmpty
            then User(user.color)
            else Opponent(opponentColor)
          val newStatus = MatchStatus.InProgress  // TODO
          new LogicImpl(newStatus, newActivePlayer, newBoard)

  override def placeOpponentDisk(): Logic =
    activePlayer match
      case User(_) => throw IllegalStateException("It is user's turn now")
      case opponent: Opponent =>
        val position = opponent.strategy.computePlacement(using board.state)
        val newBoard = board.placeDisk(opponent.color, position).captureDisks(position)
        val userColor = opponent.color.opposite
        val newActivePlayer = if board.getAvailablePlacements(userColor).isEmpty
          then Opponent(opponent.color) 
          else User(userColor)
        val newStatus = MatchStatus.InProgress  // TODO
        new LogicImpl(newStatus, newActivePlayer, newBoard)

object LogicImpl:
  
  private def getInitialActivePlayer(userColor: Color): Player = userColor match
    case Color.Black => User(Color.Black)
    case Color.White => Opponent(Color.Black)
    
  def apply(boardShape: Shape, userColor: Color): LogicImpl =
    new LogicImpl(MatchStatus.InProgress, getInitialActivePlayer(userColor), BoardImpl(boardShape))
    
  def apply(userColor: Color, board: Board) =
    new LogicImpl(MatchStatus.InProgress, getInitialActivePlayer(userColor), board)
    