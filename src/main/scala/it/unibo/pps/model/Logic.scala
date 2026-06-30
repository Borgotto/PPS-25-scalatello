package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, BoardImpl}
import it.unibo.pps.utils.Color.{Black, White}
import it.unibo.pps.utils.Player.{Opponent, User}
import it.unibo.pps.utils.{Color, MatchStatus, PlacementStrategy, Player, Position, Shape}

trait Logic:
  val activePlayer: Player
  val status: MatchStatus
  val board: Board

  def placeUserDisk(position: Position): Logic

class LogicImpl(
  override val activePlayer: Player,
  override val board: Board
) extends Logic:

  val status: MatchStatus = MatchStatus.InProgress

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
      new LogicImpl(newActivePlayer, newBoard)

object LogicImpl:
  
  private def getInitialActivePlayer(userColor: Color): Player = userColor match
    case Color.Black => User(Color.Black)
    case Color.White => Opponent(Color.Black)
    
  def apply(boardShape: Shape, userColor: Color): LogicImpl =
    new LogicImpl(getInitialActivePlayer(userColor), BoardImpl(boardShape))
    
  def apply(userColor: Color, board: Board) =
    new LogicImpl(getInitialActivePlayer(userColor), board)
    