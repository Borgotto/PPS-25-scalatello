package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, BoardImpl}
import it.unibo.pps.state.PlayerState.{Opponent, User}
import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.{Color, Shape, Status}

trait Logic:
  val matchState: MatchState

class LogicImpl(
  private val boardShape: Shape,
  private val userColor: Color,
  // TODO(eboschetti): added only to enable usage of mocks for testing purposes. Remove later.
  private[model] val board: Board
) extends Logic:

  private val userState = User(userColor)
  private val opponentState = Opponent(userColor.opposite)

  private var activePlayer = userColor match
    case Color.Black => userState
    case _ => opponentState

  private var status = Status.InProgress

  def this(boardShape: Shape, userColor: Color) =
    this(boardShape, userColor, BoardImpl(boardShape))

  override val matchState: MatchState = MatchState(status, activePlayer, board.state)
