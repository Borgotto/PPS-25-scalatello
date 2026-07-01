package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import scala.collection.immutable.HashMap
import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.utils.{Position, Color, Shape}

class BoardTest extends AnyFlatSpec:
  val BOARD_SIZE = 8
  val BOARD_SHAPE = Shape.Square(BOARD_SIZE)
  
  "A Board without disks" should "initialize itself with the disks in the correct positions" in:
    val board = Board(BOARD_SHAPE, BOARD_SIZE)
    val leftCenter = BOARD_SIZE / 2
    val rightCenter = leftCenter + 1
    val initialConfiguration = HashMap(
      Position(leftCenter, leftCenter) -> Disk(Color.White),
      Position(leftCenter, rightCenter) -> Disk(Color.Black),
      Position(rightCenter, leftCenter) -> Disk(Color.Black),
      Position(rightCenter, rightCenter) -> Disk(Color.White)
    )
    assert(board.disks.equals(initialConfiguration))

  "A Board with disks" should "be created with those disks" in:
    val expectedDisks = HashMap(
      Position(0, 0) -> Disk(Color.Black)
    )
    val board = Board(BOARD_SHAPE, BOARD_SIZE, HashMap(
      Position(0, 0) -> Disk(Color.Black)
    ))
    assert(board.disks.equals(expectedDisks))
    
  "A Board" should "know which moves are available for a given player" in:
    val leftCenter = BOARD_SIZE / 2
    val rightCenter = leftCenter + 1
    val overLeftCenter = leftCenter - 1
    val underRightCenter = rightCenter + 1
    val configuration = HashMap(
      Position(leftCenter, leftCenter) -> Disk(Color.White),
      Position(leftCenter, rightCenter) -> Disk(Color.Black),
      Position(rightCenter, leftCenter) -> Disk(Color.Black),
      Position(rightCenter, rightCenter) -> Disk(Color.White),
      Position(rightCenter, underRightCenter) -> Disk(Color.White),
      Position(5, 7) -> Disk(Color.White),
      Position(5, 8) -> Disk(Color.White)
    )
    val board = Board(BOARD_SHAPE, BOARD_SIZE, configuration)
    val expectedMovesForBlack = Set(
      Position(overLeftCenter, leftCenter),
      Position(leftCenter, overLeftCenter),
      Position(underRightCenter, underRightCenter + 1),
      Position(underRightCenter, rightCenter)
    )
    val availableMoves = board.getAvailableMoves(Color.Black)
    val diffBetweenAvailableAndExpected = availableMoves diff expectedMovesForBlack
    assert((availableMoves.size equals expectedMovesForBlack.size) && diffBetweenAvailableAndExpected.isEmpty)
