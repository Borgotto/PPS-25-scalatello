package it.unibo.pps.model.board.computations

import it.unibo.pps.domain.{Position, Shape}
import it.unibo.pps.model.board.computations.ComputationsPosExtensionsRectangle.inBounds

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}
import org.scalatest.prop.TableDrivenPropertyChecks

/** Test suite for the implementations of [[ComputationsPosExtensions]] */
class ComputationsPosExtensionsTest extends AnyFlatSpec with TableDrivenPropertyChecks:
  private val SIZE = 4
  private val HEIGHT = 4
  private val WIDTH = 6

  private val inBoundsTestTable = Table(
    ("shape", "pos", "expectedResult"),
    (Shape.Square(SIZE), Position(0, 0), true),
    (Shape.Square(SIZE), Position(SIZE, SIZE), false),
    (Shape.Rectangle(HEIGHT, WIDTH), Position(0, 0), true),
    (Shape.Rectangle(HEIGHT, WIDTH), Position(HEIGHT, WIDTH), false),
  )
  "A Position" should "know if it is in the bounds of a shape" in:
    forEvery(inBoundsTestTable):
      (shape, pos, expectedResult) =>
        shape match
          case _ => pos.inBounds(shape) should be(expectedResult)
