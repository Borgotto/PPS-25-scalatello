package it.unibo.pps.utils

import upickle.default.ReadWriter

enum Shape derives ReadWriter:
  case Square(size: Int)
  case Rectangle(height: Int, width: Int)
