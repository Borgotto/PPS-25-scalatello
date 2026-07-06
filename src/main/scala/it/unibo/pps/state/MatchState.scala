package it.unibo.pps.state

import it.unibo.pps.utils.MatchStatus

case class MatchState(
  status: MatchStatus,
  activePlayer: PlayerState,
  board: BoardState
)
