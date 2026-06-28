package it.unibo.pps.state

import it.unibo.pps.utils.{Shape, Status}

class MatchState(
  val boardShape: Shape,
  val status: Status,
  val activePlayer: PlayerState
)
