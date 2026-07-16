package it.unibo.pps.utils

import upickle.default.ReadWriter

/** Enum to represent the possible colors of disks and players.
 *
 * It also contains a method to return the [[opposite]] color.
 *
 * Possible values: [[Black]], [[White]].
 */
enum Color derives ReadWriter:
  case Black
  case White

  /** @return the opposite color: [[Black]] -> [[White]] and vice versa. */
  def opposite: Color = this match
    case Black => White
    case White => Black
