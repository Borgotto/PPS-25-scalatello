package it.unibo.pps.model

import it.unibo.pps.utils.Shape.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.be
import org.scalatest.matchers.should.Matchers.should

class LogicTest extends AnyFlatSpec:

  val SQUARE_SIZE = 8
  val SQUARE_SHAPE = Square(SQUARE_SIZE)

  val RECTANGLE_HEIGHT = 7
  val RECTANGLE_WIDTH = 9
  val RECTANGULAR_SHAPE = Rectangle(RECTANGLE_HEIGHT, RECTANGLE_WIDTH)

  "Square board" should "have correct shape and size" in:
    val logic = new LogicImpl(SQUARE_SHAPE)
    val boardShape = logic.matchState.boardShape
    boardShape match {
      case Square(n) => n should be(SQUARE_SIZE)
      case shape => fail(s"Board shape is $shape")
    }

  "Rectangular board" should "have correct shape and size" in:
    val logic = new LogicImpl(RECTANGULAR_SHAPE)
    val boardShape = logic.matchState.boardShape
    boardShape match {
      case Rectangle(h, w) =>
        h should be(RECTANGLE_HEIGHT)
        w should be(RECTANGLE_WIDTH)
      case shape => fail(s"Board shape is $shape")
    }
