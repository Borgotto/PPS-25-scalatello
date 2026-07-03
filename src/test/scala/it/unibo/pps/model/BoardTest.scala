package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.utils.{Position, Shape, Color}

import scala.collection.immutable.HashMap
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class BoardTest extends AnyFlatSpec:
  private val BOARD_SIZE: Int = 8
  private val BOARD_SHAPE: Shape = Shape.Square(BOARD_SIZE)
  private val DIST: Int = 1

  private val upLeftCenterPos: Position = Position(BOARD_SIZE / 2 - DIST, BOARD_SIZE / 2 - DIST)
  private val whiteDisk = Disk(Color.White)
  private val blackDisk = Disk(Color.Black)

  val initialDisksOnBoard: HashMap[Position, Disk] = HashMap(
    Position(upLeftCenterPos.row, upLeftCenterPos.column) -> whiteDisk,
    Position(upLeftCenterPos.row, upLeftCenterPos.column + DIST) -> blackDisk,
    Position(upLeftCenterPos.row + DIST, upLeftCenterPos.column) -> blackDisk,
    Position(upLeftCenterPos.row + DIST, upLeftCenterPos.column + DIST) -> whiteDisk
  )
  private val initialBoard: Board = Board(BOARD_SHAPE, BOARD_SIZE)
  private val validMovePosition: Position = Position(upLeftCenterPos.row - DIST, upLeftCenterPos.column)

  "A Board without disks" should "initialize itself with the disks in the correct positions" in:
    initialBoard.disks should be(initialDisksOnBoard)

  "A Board with disks" should "be created with those disks" in:
    val expectedDisks: HashMap[Position, Disk] = HashMap(
      Position(upLeftCenterPos.row, upLeftCenterPos.column + DIST) -> blackDisk
    )
    val board: Board = Board(BOARD_SHAPE, BOARD_SIZE, HashMap(
      Position(upLeftCenterPos.row, upLeftCenterPos.column + DIST) -> blackDisk
    ))
    board.disks should be(expectedDisks)
    
  "A Board" should "know which moves are available for a given player" in:
    val disksOnBoard: HashMap[Position, Disk] = initialDisksOnBoard ++ HashMap(
      Position(upLeftCenterPos.row + DIST, upLeftCenterPos.column + DIST + DIST) -> whiteDisk,
      Position(upLeftCenterPos.row + DIST, upLeftCenterPos.column + DIST + DIST + DIST) -> whiteDisk,
      Position(upLeftCenterPos.row + DIST, upLeftCenterPos.column + DIST + DIST + DIST + DIST) -> whiteDisk
    )
    val board: Board = Board(BOARD_SHAPE, BOARD_SIZE, disksOnBoard)
    val expectedAvailableMoves: Set[Position] = Set(
      Position(upLeftCenterPos.row - DIST, upLeftCenterPos.column),
      Position(upLeftCenterPos.row, upLeftCenterPos.column - DIST),
      Position(upLeftCenterPos.row + DIST + DIST, upLeftCenterPos.column + DIST + DIST + DIST),
      Position(upLeftCenterPos.row + DIST + DIST, upLeftCenterPos.column + DIST)
    )
    val availableMoves: Set[Position] = board.getAvailableMoves(Color.Black)
    availableMoves.equals(expectedAvailableMoves) should be(true)

  "A Board" should "know if a move is valid" in:
    initialBoard.isMoveValid(validMovePosition, Color.Black) should be(true)

  "A Board" should "know if a move is not valid" in:
    val notValidMovePosition: Position = Position(upLeftCenterPos.row - DIST, upLeftCenterPos.column - DIST)
    initialBoard.isMoveValid(notValidMovePosition, Color.Black) should be(false)

  "A Board" should "be able to place a new disk in a valid position" in:
    val newBoard: Board = initialBoard.placeDisk(validMovePosition, Color.Black)
    val expectedDisks: HashMap[Position, Disk] = initialBoard.disks + (validMovePosition -> blackDisk)
    val expectedBoard: Board = Board(BOARD_SHAPE, BOARD_SIZE, expectedDisks)
    newBoard.equals(expectedBoard) should be(true)

  "A Board, if the move is not valid" should "not place the new disk" in:
    val diskPosition: Position = Position(upLeftCenterPos.row - DIST, upLeftCenterPos.column - DIST)
    val newBoard: Board = initialBoard.placeDisk(diskPosition, Color.Black)
    val expectedDisks: HashMap[Position, Disk] = initialBoard.disks
    val expectedBoard: Board = Board(BOARD_SHAPE, BOARD_SIZE, expectedDisks)
    newBoard.equals(expectedBoard) should be(true)

  "A Board, after placing a disk" should "flip the correct disks" in:
    val disksOnBoard: HashMap[Position, Disk] = initialDisksOnBoard ++ HashMap(
      Position(upLeftCenterPos.row, upLeftCenterPos.column + DIST) -> whiteDisk,
      Position(upLeftCenterPos.row, upLeftCenterPos.column - DIST) -> whiteDisk,
      Position(upLeftCenterPos.row + DIST, upLeftCenterPos.column - DIST - DIST) -> whiteDisk,
      Position(upLeftCenterPos.row + DIST + DIST, upLeftCenterPos.column - DIST - DIST - DIST) -> blackDisk,
      Position(upLeftCenterPos.row + DIST, upLeftCenterPos.column + DIST + DIST) -> blackDisk,
      Position(upLeftCenterPos.row - DIST, upLeftCenterPos.column + DIST + DIST) -> blackDisk,
      Position(upLeftCenterPos.row - DIST, upLeftCenterPos.column + DIST) -> whiteDisk
    )
    val board: Board = Board(BOARD_SHAPE, BOARD_SIZE, disksOnBoard)
    val boardBeforeFlip: Board = board.placeDisk(validMovePosition, Color.Black)
    val boardAfterFlip: Board = boardBeforeFlip.flipDisks(validMovePosition, Color.Black)
    val expectedDisks: HashMap[Position, Disk] = disksOnBoard ++ HashMap(
      Position(upLeftCenterPos.row, upLeftCenterPos.column) -> blackDisk,
      Position(upLeftCenterPos.row, upLeftCenterPos.column + DIST) -> blackDisk,
      Position(upLeftCenterPos.row, upLeftCenterPos.column - DIST) -> blackDisk,
      Position(upLeftCenterPos.row + DIST, upLeftCenterPos.column - DIST - DIST) -> blackDisk,
      Position(upLeftCenterPos.row - DIST, upLeftCenterPos.column + DIST) -> blackDisk,
      validMovePosition -> blackDisk
    )
    val expectedBoard: Board = Board(BOARD_SHAPE, BOARD_SIZE, expectedDisks)
    boardAfterFlip.equals(expectedBoard) should be(true)

  "A Board" should "know if is equal to another board" in:
    initialBoard.equals(initialBoard) should be(true)
