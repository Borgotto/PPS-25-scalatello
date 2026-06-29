package it.unibo.pps.utils

enum Player(val color: Color):
  case User(c: Color) extends Player(c)
  case Opponent(c: Color) extends Player(c)
