package it.unibo.pps.utils

case class Position(row: Int, column: Int):
  def -(pos: Position): Position =
    Position(this.row - pos.row, this.column - pos.column)
