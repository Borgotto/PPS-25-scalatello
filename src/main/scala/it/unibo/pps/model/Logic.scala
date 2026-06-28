package it.unibo.pps.model

import it.unibo.pps.state.PlayerState.{Opponent, User}
import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.{Color, Shape, Status}

trait Logic:
  val matchState: MatchState

class LogicImpl(
  val boardShape: Shape,
  val userColor: Color
) extends Logic:

  private val userState = User(userColor)
  private val opponentState = Opponent(userColor.opposite)

  private var activePlayer = userColor match
    case Color.Black => userState
    case _ => opponentState
  
  private var status = Status.InProgress

  override val matchState: MatchState = MatchState(boardShape, status, activePlayer)
