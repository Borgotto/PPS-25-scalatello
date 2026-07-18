package it.unibo.pps.domain

import upickle.default.ReadWriter

/** Enum to represent the possible colors of a [[Disk]] and a [[Player]].
 *
 * This provides a method that given a color returns the other: [[Color.opposite]].
 *
 * Possible values: [[Black]], [[White]].
 */
enum Color derives ReadWriter:
  case Black
  case White

  /** @return the other color value. */
  def opposite: Color = this match
    case Black => White
    case White => Black
