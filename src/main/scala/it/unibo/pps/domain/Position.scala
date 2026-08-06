package it.unibo.pps.domain

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
      case (r, c) if r.equals(0) && c.equals(0) => Position(r, c)
      case (r, c) if r.equals(0) => Position(r, column / c)
      case (r, c) if c.equals(0) => Position(row / r, c)
      case (_, _) => Position(row / pos.row, column / pos.column)

  /** @return the position to the left of this. */
  def left: Position = Position(row, column - 1)

  /** @return the position to the right of this. */
  def right: Position = Position(row, column + 1)

  /** @return the position on top of this. */
  def up: Position = Position(row - 1, column)

  /** @return the position below this. */
  def down: Position = Position(row + 1, column)

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
