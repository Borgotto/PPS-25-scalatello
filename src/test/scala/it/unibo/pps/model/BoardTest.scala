package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.state.{BoardState, DiskState}
import it.unibo.pps.utils.{Color, Position, Shape}

import scala.collection.immutable.HashMap
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class BoardTest extends AnyFlatSpec:
  private val BOARD_SIZE: Int = 8
  private val BOARD_SQUARE: Shape = Shape.Square(BOARD_SIZE)
  private val BOARD_HEIGHT: Int = 6
  private val BOARD_WIDTH: Int = 8
  private val BOARD_RECTANGLE: Shape = Shape.Rectangle(BOARD_HEIGHT, BOARD_WIDTH)
  private val DIST: Int = 1

  private val whiteDisk = Disk(Color.White)
  private val blackDisk = Disk(Color.Black)

  private def getTopLeftCenterPos(shape: Shape): Position =
    shape match
      case Shape.Square(n) => Position(BOARD_SIZE / 2 - DIST, BOARD_SIZE / 2 - DIST)
      case Shape.Rectangle(h, w) => Position(BOARD_HEIGHT / 2 - DIST, BOARD_WIDTH / 2 - DIST)
      
  private def getInitialDisksOnBoard(shape: Shape): HashMap[Position, Disk] =
    HashMap(
      Position(getTopLeftCenterPos(shape).row, getTopLeftCenterPos(shape).column) -> whiteDisk,
      Position(getTopLeftCenterPos(shape).row, getTopLeftCenterPos(shape).column + DIST) -> blackDisk,
      Position(getTopLeftCenterPos(shape).row + DIST, getTopLeftCenterPos(shape).column) -> blackDisk,
      Position(getTopLeftCenterPos(shape).row + DIST, getTopLeftCenterPos(shape).column + DIST) -> whiteDisk
    )
  private val initialSquareBoard: Board = Board(BOARD_SQUARE)
  private val initialRectangleBoard: Board = Board(BOARD_RECTANGLE)
  private val validMovePosition: Position = Position(getTopLeftCenterPos(BOARD_SQUARE).row - DIST, getTopLeftCenterPos(BOARD_SQUARE).column)

  "A Board without disks" should "initialize itself with the disks in the correct positions" in:
    initialSquareBoard.disks should be(getInitialDisksOnBoard(BOARD_SQUARE))
    initialRectangleBoard.disks should be(getInitialDisksOnBoard(BOARD_RECTANGLE))

  "A Board with disks" should "be created with those disks" in:
    val expectedDisks: HashMap[Position, Disk] = HashMap(
      Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column + DIST) -> blackDisk
    )
    val board: Board = Board(BOARD_SQUARE, HashMap(
      Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column + DIST) -> blackDisk
    ))
    board.disks should be(expectedDisks)
    
  "A Board" should "know which moves are available for a given player" in:
    val disksOnBoard: HashMap[Position, Disk] = getInitialDisksOnBoard(BOARD_SQUARE) ++ HashMap(
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST + DIST) -> whiteDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST + DIST + DIST) -> whiteDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST + DIST + DIST + DIST) -> whiteDisk
    )
    val board: Board = Board(BOARD_SQUARE, disksOnBoard)
    val expectedAvailableMoves: Set[Position] = Set(
      Position(getTopLeftCenterPos(BOARD_SQUARE).row - DIST, getTopLeftCenterPos(BOARD_SQUARE).column),
      Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column - DIST),
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST + DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST + DIST + DIST),
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST + DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST)
    )
    val availableMoves: Set[Position] = board.getAvailableMoves(Color.Black)
    availableMoves.equals(expectedAvailableMoves) should be(true)

  "A Board" should "know if a move is valid" in:
    initialSquareBoard.isMoveValid(validMovePosition, Color.Black) should be(true)

  "A Board" should "know if a move is not valid" in:
    val notValidMovePosition: Position = 
      Position(getTopLeftCenterPos(BOARD_SQUARE).row - DIST, getTopLeftCenterPos(BOARD_SQUARE).column - DIST)
    initialSquareBoard.isMoveValid(notValidMovePosition, Color.Black) should be(false)

  "A Board" should "be able to place a new disk in a valid position" in:
    val newBoard: Board = initialSquareBoard.placeDisk(validMovePosition, Color.Black)
    val expectedDisks: HashMap[Position, Disk] = initialSquareBoard.disks + (validMovePosition -> blackDisk)
    val expectedBoard: Board = Board(BOARD_SQUARE, expectedDisks)
    newBoard.equals(expectedBoard) should be(true)

  "A Board, if the move is not valid" should "not place the new disk" in:
    val diskPosition: Position = Position(getTopLeftCenterPos(BOARD_SQUARE).row - DIST, getTopLeftCenterPos(BOARD_SQUARE).column - DIST)
    val newBoard: Board = initialSquareBoard.placeDisk(diskPosition, Color.Black)
    val expectedDisks: HashMap[Position, Disk] = initialSquareBoard.disks
    val expectedBoard: Board = Board(BOARD_SQUARE, expectedDisks)
    newBoard.equals(expectedBoard) should be(true)

  "A Board, after placing a disk" should "flip the correct disks" in:
    val disksOnBoard: HashMap[Position, Disk] = getInitialDisksOnBoard(BOARD_SQUARE) ++ HashMap(
      Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column + DIST) -> whiteDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column - DIST) -> whiteDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST, getTopLeftCenterPos(BOARD_SQUARE).column - DIST - DIST) -> whiteDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST + DIST, getTopLeftCenterPos(BOARD_SQUARE).column - DIST - DIST - DIST) -> blackDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST + DIST) -> blackDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row - DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST + DIST) -> blackDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row - DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST) -> whiteDisk
    )
    val board: Board = Board(BOARD_SQUARE, disksOnBoard)
    val boardBeforeFlip: Board = board.placeDisk(validMovePosition, Color.Black)
    val boardAfterFlip: Board = boardBeforeFlip.flipDisks(validMovePosition, Color.Black)
    val expectedDisks: HashMap[Position, Disk] = disksOnBoard ++ HashMap(
      Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column) -> blackDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column + DIST) -> blackDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column - DIST) -> blackDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST, getTopLeftCenterPos(BOARD_SQUARE).column - DIST - DIST) -> blackDisk,
      Position(getTopLeftCenterPos(BOARD_SQUARE).row - DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST) -> blackDisk,
      validMovePosition -> blackDisk
    )
    val expectedBoard: Board = Board(BOARD_SQUARE, expectedDisks)
    boardAfterFlip.equals(expectedBoard) should be(true)

  "A Board" should "know if is equal to another board" in:
    initialSquareBoard.equals(initialSquareBoard) should be(true)

  "A Board" should "return its correct state" in:
    val boardState: BoardState = initialSquareBoard.state
    val diskStates: Seq[DiskState] = Seq(
      DiskState(whiteDisk.color, Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column)),
      DiskState(blackDisk.color, Position(getTopLeftCenterPos(BOARD_SQUARE).row, getTopLeftCenterPos(BOARD_SQUARE).column + DIST)),
      DiskState(blackDisk.color, Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST, getTopLeftCenterPos(BOARD_SQUARE).column)),
      DiskState(whiteDisk.color, Position(getTopLeftCenterPos(BOARD_SQUARE).row + DIST, getTopLeftCenterPos(BOARD_SQUARE).column + DIST))
    )
    val expectedState: BoardState = BoardState(BOARD_SQUARE, diskStates)
    boardState.shape.equals(expectedState.shape) should be(true)
    boardState.disks.toSet.equals(expectedState.disks.toSet) should be(true)
