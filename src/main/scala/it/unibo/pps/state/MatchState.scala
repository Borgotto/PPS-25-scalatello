package it.unibo.pps.state

import it.unibo.pps.utils.MatchStatus

import upickle.default.ReadWriter

case class MatchState(
  status: MatchStatus,
  activePlayer: PlayerState,
  board: BoardState
) derives ReadWriter
