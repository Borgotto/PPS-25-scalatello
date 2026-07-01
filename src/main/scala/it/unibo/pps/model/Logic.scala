package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, BoardImpl}
import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.Player.{Opponent, User}
import it.unibo.pps.utils.{Color, MatchStatus, Player, Position, Shape}

trait Logic:
  val state: MatchState
  def placeUserDisk(position: Position): Logic

class LogicImpl(
  private val status: MatchStatus,
  private val activePlayer: Player,
  private val board: Board
) extends Logic:

  override val state = MatchState(status, activePlayer.state, board.state)

  override def placeUserDisk(position: Position): Logic =
    activePlayer match
      case Opponent(_) => throw IllegalStateException("It is opponent's turn now")
      case _ => ()
    val userColor = activePlayer.color
    val opponentColor = activePlayer.color.opposite
    if !board.isPlacementValid(userColor, position) then this
    else
      val newBoard = board.placeDisk(userColor, position).captureDisks(position)
      val newActivePlayer = if board.getAvailablePlacements(opponentColor).isEmpty
        then User(userColor)
        else Opponent(opponentColor)
      val newStatus = MatchStatus.InProgress
      new LogicImpl(newStatus, newActivePlayer, newBoard)

object LogicImpl:
  
  private def getInitialActivePlayer(userColor: Color): Player = userColor match
    case Color.Black => User(Color.Black)
    case Color.White => Opponent(Color.Black)
    
  def apply(boardShape: Shape, userColor: Color): LogicImpl =
    new LogicImpl(MatchStatus.InProgress, getInitialActivePlayer(userColor), BoardImpl(boardShape))
    
  def apply(userColor: Color, board: Board) =
    new LogicImpl(MatchStatus.InProgress, getInitialActivePlayer(userColor), board)
    