package it.unibo.pps.domain

import upickle.default.ReadWriter

/** Enum to represent the possible colors of a [[Disk]] and a [[Player]]. */
enum Color derives ReadWriter:
  case Black
  case White

  /** @return the other color value. */
  def opposite: Color = this match
    case Black => White
    case White => Black
