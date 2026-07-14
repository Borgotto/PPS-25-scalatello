package it.unibo.pps.model.board

import it.unibo.pps.utils.{Position, Shape}

import scala.annotation.tailrec
import scala.math.Ordering.Int

class PosComputeExtensions:
  @tailrec
  private def getPosOnSameDiagonal(source: Position, destination: Position,
                                   direction: Position, acc: Set[Position] = Set()): Set[Position] =
    val nextPos: Position = source - direction
    (nextPos, destination) match
      case (f, s) if f.equals(s) => acc
      case (f, s) => getPosOnSameDiagonal(f, s, direction, acc + f)
      
  extension (x: Int)
    private def inRange(y: Int, z: Int): Boolean =
      x <= Int.max(y, z) && x >= Int.min(y, z)

    private def inBetween(y: Int, z: Int): Boolean =
      x.inRange(Int.min(y, z) + 1, Int.max(y, z) - 1)

  extension (p: Position)
    private def /(pos: Position): Position =
      (pos.row, pos.column) match
        case (r, c) if r.equals(0) && c.equals(0) => Position(r, c)
        case (r, c) if r.equals(0) => Position(r, p.column / c)
        case (r, c) if c.equals(0) => Position(p.row / r, c)
        case (_, _) => Position(p.row / pos.row, p.column / pos.column)

    private def onSameDiagonal(firstPos: Position, secondPos: Position): Boolean =
      val distance: Position = firstPos - secondPos
      val direction: Position = distance / Position(distance.row.abs, distance.column.abs)
      getPosOnSameDiagonal(firstPos, secondPos, direction).contains(p) &&
        distance.row.abs.equals(distance.column.abs)
    
    def inBounds(shape: Shape): Boolean =
      val minPosition: Position = Position(0, 0)
      p.row.inRange(minPosition.row, shape.maxRow) && p.column.inRange(minPosition.column, shape.maxColumn)

    def inBetween(firstPos: Position, secondPos: Position): Boolean =
      (firstPos, secondPos) match
        case (f, s) if f.row.equals(s.row) => p.column.inBetween(f.column, s.column) && p.row.equals(f.row)
        case (f, s) if f.column.equals(s.column) => p.row.inBetween(f.row, s.row) && p.column.equals(f.column)
        case (f, s) => p.onSameDiagonal(f, s)
    
    def inNeighbourhood(pos: Position): Boolean =
      val maxDistance: Int = 1
      val distance: Position = p - pos
      distance.row.abs <= maxDistance && distance.column.abs <= maxDistance
