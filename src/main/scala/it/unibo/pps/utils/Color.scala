package it.unibo.pps.utils

enum Color:
  case Black
  case White

  def opposite: Color = this match
    case Black => White
    case White => Black
