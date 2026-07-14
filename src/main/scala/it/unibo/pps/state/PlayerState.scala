package it.unibo.pps.state

import it.unibo.pps.utils.Color
import it.unibo.pps.model.strategy.{UserPlacementStrategy, OpponentPlacementStrategy}

import upickle.default.ReadWriter

enum PlayerState(val color: Color, val strategy: UserPlacementStrategy | OpponentPlacementStrategy) derives ReadWriter:
  case User(c: Color, s: UserPlacementStrategy) extends PlayerState(c, s)
  case Opponent(c: Color, s: OpponentPlacementStrategy) extends PlayerState(c, s)
