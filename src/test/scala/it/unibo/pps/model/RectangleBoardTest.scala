package it.unibo.pps.model

import it.unibo.pps.utils.Shape

import org.scalatest.flatspec.AnyFlatSpec

class RectangleBoardTest extends AnyFlatSpec:
  private val BOARD_HEIGHT: Int = 4
  private val BOARD_WIDTH: Int = 6
  private val BOARD_SHAPE = Shape.Rectangle(BOARD_HEIGHT, BOARD_WIDTH)

  private val boardTest = BoardTest(BOARD_SHAPE)

  "A Rectangle Board without disks" should "initialize itself with the disks in the correct positions" in:
    boardTest.creationInitialBoardTest()

  "A Rectangle Board with disks" should "be created with those disks" in:
    boardTest.creationBoardWithDisksTest()

  "A Rectangle Board" should "know which moves are available for a given player" in:
    boardTest.availableMovesTest()

  "A Rectangle Board" should "know if a move is valid" in:
    boardTest.availableMovesTest()

  "A Rectangle Board" should "know if a move is not valid" in:
    boardTest.notValidMoveTest()

  "A Rectangle Board" should "know if is equal to another board" in:
    boardTest.equalsTest()

  "A Rectangle Board" should "know if is not equal to another board" in :
    boardTest.notEqualTest()

  "A Rectangle Board" should "be able to place a new disk in a valid position" in:
    boardTest.placeDiskInValidMoveTest()

  "A Rectangle Board, if the move is not valid" should "not place the new disk" in:
    boardTest.placeDiskInNotValidMoveTest()

  "A Rectangle Board, after placing a disk" should "capture the correct disks" in:
    boardTest.captureDisksTest()

  "A Rectangle Board" should "return its correct state" in:
    boardTest.boardStateTest()
