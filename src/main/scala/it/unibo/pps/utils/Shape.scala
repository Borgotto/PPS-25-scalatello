package it.unibo.pps.utils

import upickle.default.ReadWriter

enum Shape derives ReadWriter:
  case Square(size: Int)
  case Rectangle(height: Int, width: Int)
  
  def maxRow: Int = this match
    case Square(size) => size - 1
    case Rectangle(height, _) => height - 1
  
  def maxColumn: Int = this match
    case Square(size) => size - 1
    case Rectangle(_, width) => width - 1
    