package it.unibo.pps.utils

import upickle.default.ReadWriter

/** Represents a two-dimensional position.
 *
 * It has methods to:
 * - subtract a position from another [[-]];
 * - get the position on the: [[left]], [[right]] or [[up]] and [[down]].
 *
 * @param row the row of a [[Board]].
 * @param column the column of a [[Board]].
 */
case class Position(row: Int, column: Int) derives ReadWriter:
  /**
   * @param pos the position to subtract.
   * @return the subtraction of this [[Position]] and another.
   */
  def -(pos: Position): Position = Position(this.row - pos.row, this.column - pos.column)

  /** @return the position to the left of this. */
  def left: Position = Position(row, column - 1)

  /** @return the position to the right of this. */
  def right: Position = Position(row, column + 1)
  /** @return the position on top of this. */
  def up: Position = Position(row - 1, column)
  /** @return the position below this. */
  def down: Position = Position(row + 1, column)
  /** @inheritdoc */
  override def toString: String = s"($row,$column)"

/** Contains three implicit conversions:
 * - from [[scala.Tuple2]] of ([[int]],[[Int]]) to [[Position]];
 * - from [[scala.Tuple2]] of ([[String]],[[String]]) to [[Position]];
 * - from [[String]] to [[Position]].
 *
 * @example {{{
 *  import it.unibo.pps.utils.Position
 *
 *  first conversion:
 *  val pos: Position = (0,0)
 *
 *  second conversion:
 *  val pos: Position = ("0","0")
 *
 *  third conversion:
 *  val pos: Position = "(0,0)" }}}
 */
object Position:
  given Conversion[(Int, Int), Position] = (x, y) =>
    Position(x, y)

  given Conversion[(String, String), Position] = (x, y) =>
    (x.toInt, y.toInt)

  given Conversion[String, Position] = s =>
    val parts = s.split(",").map(_.trim)
    (parts(0), parts(1))