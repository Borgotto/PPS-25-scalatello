package it.unibo.pps.state

import it.unibo.pps.domain.{ActivePlayer, MatchStatus}
import it.unibo.pps.model.player.{Opponent, User}

import upickle.default.ReadWriter

case class MatchState(
  status: MatchStatus,
  user: User,
  opponent: Opponent,
  activePlayer: ActivePlayer,
  board: BoardState
) derives ReadWriter
