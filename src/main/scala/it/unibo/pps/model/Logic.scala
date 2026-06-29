package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, BoardImpl}
import it.unibo.pps.utils.Player.{Opponent, User}
import it.unibo.pps.utils.{Color, MatchStatus, PlacementStrategy, Player, Position, Shape}

trait Logic:
  val activePlayer: Player
  val status: MatchStatus
  val board: Board

  def placeUserDisk(position: Position): Logic

class LogicImpl(
  private val boardShape: Shape,
  private val userColor: Color,
  override val board: Board
) extends Logic:

  def this(boardShape: Shape, userColor: Color) =
    this(boardShape, userColor, BoardImpl(boardShape))

  private val user = User(userColor)
  private val opponent = Opponent(userColor.opposite)

  val activePlayer: Player = userColor match
    case Color.Black => user
    case _ => opponent

  val status: MatchStatus = MatchStatus.InProgress

  override def placeUserDisk(position: Position): Logic =
    val strategy: PlacementStrategy = _ => position
    val newBoard = board.placeDisk(user.color, strategy)
    LogicImpl(boardShape, userColor, newBoard)
