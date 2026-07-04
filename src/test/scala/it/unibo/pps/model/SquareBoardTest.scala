package it.unibo.pps.model

import it.unibo.pps.utils.Shape

import org.scalatest.flatspec.AnyFlatSpec

class SquareBoardTest extends AnyFlatSpec:
  private val BOARD_SIZE: Int = 4
  private val BOARD_SHAPE: Shape = Shape.Square(BOARD_SIZE)

  private val boardTest = BoardTest(BOARD_SHAPE)

  "A Square Board without disks" should "initialize itself with the disks in the correct positions" in:
    boardTest.creationInitialBoardTest()

  "A Square Board with disks" should "be created with those disks" in:
    boardTest.creationBoardWithDisksTest()

  "A Square Board" should "know which moves are available for a given player" in:
    boardTest.availableMovesTest()

  "A Square Board" should "know if a move is valid" in:
    boardTest.validMoveTest()

  "A Square Board" should "know if a move is not valid" in:
    boardTest.notValidMoveTest()

  "A Square Board" should "know if is equal to another board" in:
    boardTest.equalsTest()

  "A Square Board" should "know if is not equal to another board" in:
    boardTest.notEqualTest()

  "A Square Board" should "be able to place a new disk in a valid position" in:
    boardTest.placeDiskInValidMoveTest()

  "A Square Board, if the move is not valid" should "not place the new disk" in:
    boardTest.placeDiskInNotValidMoveTest()

  "A Square Board, after placing a disk" should "capture the correct disks" in:
    boardTest.captureDisksTest()

  "A Square Board" should "return its correct state" in:
    boardTest.boardStateTest()
