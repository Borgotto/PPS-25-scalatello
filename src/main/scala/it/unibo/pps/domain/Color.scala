package it.unibo.pps.domain

import upickle.default.ReadWriter

/** Represents the possible colors of a [[model.board.Disk]]. */
enum Color derives ReadWriter:
  case Black
  case White

  /** @return the other color. */
  def opposite: Color = this match
    case Black => White
    case White => Black
