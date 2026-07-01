package it.unibo.pps.utils

import it.unibo.pps.state.PlayerState

enum Player(val color: Color):
  case User(c: Color) extends Player(c)
  case Opponent(c: Color) extends Player(c)

  def strategy: PlacementStrategy = this match
    case User(_)     => PlacementStrategy.User
    case Opponent(_) => PlacementStrategy.Opponent

  def state: PlayerState = this match
    case User(c) => PlayerState.User(c)
    case Opponent(c) => PlayerState.Opponent(c)
