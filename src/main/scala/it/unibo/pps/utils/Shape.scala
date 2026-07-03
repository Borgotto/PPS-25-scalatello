package it.unibo.pps.utils

enum Shape(val height: Int, val width: Int):
  case Square(size: Int) extends Shape(size, size)
  case Rectangle(h: Int, w: Int) extends Shape(h, w)
