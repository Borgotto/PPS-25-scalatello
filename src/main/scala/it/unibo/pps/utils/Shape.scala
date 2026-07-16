package it.unibo.pps.utils

import upickle.default.ReadWriter

/** Enum to represent the possible shapes of a [[Board]].
 * 
 * It also contains:
 * - a method to get the [[maxRow]];
 * - a method to get the [[maxColumn]].
 * 
 * Possible values: [[Square]], [[Rectangle]].
 */
enum Shape derives ReadWriter:
  case Square(size: Int)
  case Rectangle(height: Int, width: Int)

  /** @return the maximum row of the shape. */
  def maxRow: Int = this match
    case Square(size) => size - 1
    case Rectangle(height, _) => height - 1

  /** @return the maximum column of the shape. */
  def maxColumn: Int = this match
    case Square(size) => size - 1
    case Rectangle(_, width) => width - 1
    