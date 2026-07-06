package it.unibo.pps.state

import it.unibo.pps.utils.Color

enum PlayerState(val color: Color):
  case User(c: Color) extends PlayerState(c)
  case Opponent(c: Color) extends PlayerState(c)
