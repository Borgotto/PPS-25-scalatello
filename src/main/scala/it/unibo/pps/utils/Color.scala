package it.unibo.pps.utils

import upickle.default.ReadWriter

enum Color derives ReadWriter:
  case Black
  case White

  def opposite: Color = this match
    case Black => White
    case White => Black
