package it.unibo.pps.utils

import upickle.default.ReadWriter

case class Position(row: Int, column: Int) derives ReadWriter:
  def -(pos: Position): Position = Position(this.row - pos.row, this.column - pos.column)
  def left: Position = Position(row, column - 1)
  def right: Position = Position(row, column + 1)
  def up: Position = Position(row - 1, column)
  def down: Position = Position(row + 1, column)
  override def toString: String = s"($row,$column)"

object Position:
  given Conversion[(Int, Int), Position] = (x, y) =>
    Position(x, y)

  given Conversion[(String, String), Position] = (x, y) =>
    (x.toInt, y.toInt)

  given Conversion[String, Position] = s =>
    val parts = s.split(",").map(_.trim)
    (parts(0), parts(1))