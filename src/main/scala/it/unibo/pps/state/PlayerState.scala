package it.unibo.pps.state

import it.unibo.pps.utils.Color

enum PlayerState:
  case User(color: Color)
  case Opponent(color: Color)
