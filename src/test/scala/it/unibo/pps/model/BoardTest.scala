package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.utils.{Color, Position, Shape}
import scala.collection.immutable.HashMap
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{should, shouldBe, empty, be, equal}

class BoardTest extends AnyFlatSpec:
  val BOARD_SIZE = 8
  val BOARD_SHAPE = Shape.Square(BOARD_SIZE)
  val STEP = 1

  val left: Int = BOARD_SIZE / 2 - STEP
  val right: Int = left + STEP
  
  "A Board without disks" should "initialize itself with the disks in the correct positions" in:
    val board = Board(BOARD_SHAPE, BOARD_SIZE)
    val initialConfiguration = HashMap(
      Position(left, left) -> Disk(Color.White),
      Position(left, right) -> Disk(Color.Black),
      Position(right, left) -> Disk(Color.Black),
      Position(right, right) -> Disk(Color.White)
    )
    board.disks should be(initialConfiguration)

  "A Board with disks" should "be created with those disks" in:
    val expectedDisks = HashMap(
      Position(left, right) -> Disk(Color.Black)
    )
    val board = Board(BOARD_SHAPE, BOARD_SIZE, HashMap(
      Position(left, right) -> Disk(Color.Black)
    ))
    board.disks should be(expectedDisks)
    
  "A Board" should "know which moves are available for a given player" in:
    val configuration = HashMap(
      Position(left, left) -> Disk(Color.White),
      Position(left, right) -> Disk(Color.Black),
      Position(right, left) -> Disk(Color.Black),
      Position(right, right) -> Disk(Color.White),
      Position(right, right + STEP) -> Disk(Color.White),
      Position(right, right + STEP + STEP) -> Disk(Color.White),
      Position(right, right + STEP + STEP + STEP) -> Disk(Color.White)
    )
    val board = Board(BOARD_SHAPE, BOARD_SIZE, configuration)
    val expectedMovesForBlack = Set(
      Position(left - STEP, left),
      Position(left, left - STEP),
      Position(right + STEP, right + STEP + STEP),
      Position(right + STEP, right)
    )
    val availableMoves = board.getAvailableMoves(Color.Black)
    val diffBetweenAvailableAndExpected = availableMoves diff expectedMovesForBlack
    diffBetweenAvailableAndExpected shouldBe empty
    availableMoves.size should equal (expectedMovesForBlack.size)
