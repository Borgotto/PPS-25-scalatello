package it.unibo.pps.domain

import upickle.default.ReadWriter

/** Represents the possible shapes of a [[model.board.Board]]. */
enum Shape derives ReadWriter:
  case Square(size: Int)
  case Rectangle(height: Int, width: Int)

  /** @return the maximum row index of the shape. */
  def maxRowIndex: Int = this match
    case Square(size) => size - 1
    case Rectangle(height, _) => height - 1

  /** @return the maximum column index of the shape. */
  def maxColumnIndex: Int = this match
    case Square(size) => size - 1
    case Rectangle(_, width) => width - 1
    