package it.unibo.pps.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}
import org.scalatest.prop.TableDrivenPropertyChecks

/** Test suite for [[Position]] */
class PositionTest extends AnyFlatSpec with TableDrivenPropertyChecks:
  private val initialPos: Position = (2, 3)

  "A subtraction between positions" should "subtract both coordinates" in:
    initialPos - Position(1, 2) should be(Position(1, 1))

  private val divisionTestTable = Table(
    ("result",                    "expectedResult"),
    (initialPos / Position(0, 0), Position(0, 0)),
    (initialPos / Position(0, 1), Position(0, initialPos.column)),
    (initialPos / Position(1, 0), Position(initialPos.row, 0)),
    (initialPos / Position(2, 1), Position(1, 3))
  )
  "This division of two positions" should "divide both coordinates, ignoring the division with 0" in:
    forEvery(divisionTestTable):
      (result, expectedResult) =>
        result should be(expectedResult)

  private val directionTestTable = Table(
    ("posOnDirection", "expectedPos"),
    (initialPos.left,  Position(initialPos.row, 2)),
    (initialPos.right, Position(initialPos.row, 4)),
    (initialPos.up,    Position(1, initialPos.column)),
    (initialPos.down,  Position(3, initialPos.column))
  )
  "If asked a position on a direction, it" should "return the correct one" in:
    forEvery(directionTestTable):
      (posOnDirection, expectedPos) =>
        posOnDirection should be(expectedPos)

  private val onSameDiagonalTestTable = Table(
    ("firstPos",     "secondPos",    "betweenPos",  "expectedResult"),
    (Position(0, 0), Position(3, 3), Position(1, 1), true),
    (Position(0, 0), Position(1, 1), Position(2, 2), false)
  )
  "A Position" should "know if it is on the same diagonal and between two positions" in :
    forEvery(onSameDiagonalTestTable):
      (firstPos, secondPos, betweenPos, expectedResult) =>
        betweenPos.onSameDiagonal(firstPos, secondPos) should be(expectedResult)

  private val inBetweenTestTable = Table(
    ("firstPos",     "secondPos",    "betweenPos",   "expectedResult"),
    (Position(0, 0), Position(0, 5), Position(0, 3), true),
    (Position(0, 0), Position(0, 3), Position(0, 5), false),
    (Position(5, 0), Position(5, 5), Position(5, 3), true),
    (Position(5, 0), Position(5, 3), Position(5, 5), false),
  )
  "A Position" should "know if it is between two positions" in :
    forEvery(inBetweenTestTable):
      (firstPos, secondPos, betweenPos, expectedResult) =>
        betweenPos.inBetweenPos(firstPos, secondPos) should be(expectedResult)

  private val inNeighbourhoodTestTable = Table(
    ("pos",          "neighbourPos", "expectedResult"),
    (Position(1, 1), Position(0, 0), true),
    (Position(1, 1), Position(0, 1), true),
    (Position(1, 1), Position(1, 0), true),
    (Position(1, 1), Position(2, 2), true),
    (Position(1, 1), Position(3, 3), false)
  )
  "A Position" should "know if it is in the neighbourhood of another position" in :
    forEvery(inNeighbourhoodTestTable):
      (pos, neighbourPos, expectedResult) =>
        pos.inNeighbourhood(neighbourPos) should be(expectedResult)

  "Using the toString on a Position" should "result in (row,column)" in:
    initialPos.toString should be("(2,3)")
