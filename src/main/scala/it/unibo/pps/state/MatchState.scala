package it.unibo.pps.state

import it.unibo.pps.utils.Status

class MatchState(
  val status: Status,
  val activePlayer: PlayerState,
  val board: BoardState
)
