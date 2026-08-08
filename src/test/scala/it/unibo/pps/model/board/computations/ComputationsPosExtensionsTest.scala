package it.unibo.pps.model.board.computations

import it.unibo.pps.domain.{Position, Shape}
import it.unibo.pps.model.board.computations.ComputationsPosExtensionsRectangle.inBounds

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}
import org.scalatest.prop.TableDrivenPropertyChecks

/** Test suite for the implementations of [[ComputationsPosExtensions]] */
class ComputationsPosExtensionsTest extends AnyFlatSpec with TableDrivenPropertyChecks:
  private val BOARD_SIZE = 4
  private val BOARD_HEIGHT = 4
  private val BOARD_WIDTH = 6

  private val inBoundsTestTable = Table(
    ("shape", "pos", "expectedResult"),
    (Shape.Square(BOARD_SIZE), Position(0, 0), true),
    (Shape.Square(BOARD_SIZE), Position(BOARD_SIZE, BOARD_SIZE), false),
    (Shape.Rectangle(BOARD_HEIGHT, BOARD_WIDTH), Position(0, 0), true),
    (Shape.Rectangle(BOARD_HEIGHT, BOARD_WIDTH), Position(BOARD_HEIGHT, BOARD_WIDTH), false),
  )
  "A Position" should "know if it is in the bounds of a shape" in:
    forEvery(inBoundsTestTable):
      (shape, pos, expectedResult) =>
        shape match
          case _ => pos.inBounds(shape) should be(expectedResult)
