package it.unibo.pps.domain

import it.unibo.pps.utils.IntExtensions.inBetween

import scala.annotation.tailrec

import upickle.default.ReadWriter

/** Represents a two-dimensional position.
 *  @param row the row of a [[model.board.Board]].
 *  @param column the column of a [[model.board.Board]].
 */
case class Position(row: Int, column: Int) derives ReadWriter:
  /**
   *  @param pos the position to subtract.
   *  @return the subtraction of another position from this.
   */
  def -(pos: Position): Position = Position(this.row - pos.row, this.column - pos.column)

  /** This divides two position instances.
   *
   *  This ignores the division with 0, if a number is divided by 0 the result is 0.
   *
   *  @param pos the position to divide this position with.
   *  @return the result of the division.
   */
  def /(pos: Position): Position =
    (pos.row, pos.column) match
      case (r, c) if r.equals(0) && c.equals(0) => (r, c)
      case (r, c) if r.equals(0) => (r, column / c)
      case (r, c) if c.equals(0) => (row / r, c)
      case (_, _) => (row / pos.row, column / pos.column)

  /** @return the position to the left of this. */
  def left: Position = (row, column - 1)

  /** @return the position to the right of this. */
  def right: Position = (row, column + 1)

  /** @return the position on top of this. */
  def up: Position = (row - 1, column)

  /** @return the position below this. */
  def down: Position = (row + 1, column)

  /** Used to know if this position is on the same diagonal of `firstPos` and `secondPos` and also between them.
   *
   *  @param firstPos  the starting position to be considered on the diagonal.
   *  @param secondPos the last position to be considered on the diagonal.
   *  @return `true` if the position is on the same diagonal and between `firstPos` and `secondPos`, `false` otherwise.
   */
  def onSameDiagonal(firstPos: Position, secondPos: Position): Boolean =
    @tailrec
    def _getPosOnSameDiagonal(source: Position, destination: Position,
                              direction: Position, acc: Set[Position] = Set()): Set[Position] =
      val nextPos: Position = source - direction
      (nextPos, destination) match
        case (f, s) if f.equals(s) => acc
        case (f, s) => _getPosOnSameDiagonal(f, s, direction, acc + f)

    val distance: Position = firstPos - secondPos
    val direction: Position = distance / Position(distance.row.abs, distance.column.abs)
    _getPosOnSameDiagonal(firstPos, secondPos, direction).contains(this) &&
      distance.row.abs.equals(distance.column.abs)

  /** This is used to know if this position is between `firstPos` and `secondPos` vertically, horizontally or diagonally.
   *
   *  @param firstPos  the starting position to consider.
   *  @param secondPos the last position to consider.
   *  @return `true` if this position is between `firstPos` and `secondPos`, `false` otherwise.
   */
  def inBetweenPos(firstPos: Position, secondPos: Position): Boolean =
    (firstPos, secondPos) match
      case (f, s) if f.row.equals(s.row) => this.column.inBetween(f.column, s.column) && this.row.equals(f.row)
      case (f, s) if f.column.equals(s.column) => this.row.inBetween(f.row, s.row) && this.column.equals(f.column)
      case (f, s) => this.onSameDiagonal(f, s)

  /** This is used to determine if this position is in the neighbourhood of `pos`.
   *
   *  Being in the neighbourhood of a position means that the distance between them is inside the range [-1, 1].
   *
   *  @param pos the position of which to consider the neighbourhood of.
   *  @return `true` if this position is in the neighbourhood of `pos`, `false` otherwise.
   */
  def inNeighbourhood(pos: Position): Boolean =
    val maxDistance: Int = 1
    val distance: Position = this - pos
    distance.row.abs <= maxDistance && distance.column.abs <= maxDistance
  
  override def toString: String = s"($row,$column)"

/** This object contains three implicit conversions:
 *    - from [[scala.Tuple2]] of ([[Int]],[[Int]]) to [[Position]];
 *    - from [[scala.Tuple2]] of ([[String]],[[String]]) to [[Position]];
 *    - from [[String]] to [[Position]].
 *
 *  @example
 *  {{{import it.unibo.pps.utils.Position
 *
 *    first conversion:
 *    val pos: Position = (0,0)
 *
 *    second conversion:
 *    val pos: Position = ("0","0")
 *
 *    third conversion:
 *    val pos: Position = "(0,0)" }}}
 */
object Position:
  given Conversion[(Int, Int), Position] = (x, y) =>
    Position(x, y)

  given Conversion[(String, String), Position] = (x, y) =>
    (x.toInt, y.toInt)

  given Conversion[String, Position] = s =>
    val parts = s.split(",").map(_.trim)
    (parts(0), parts(1))
