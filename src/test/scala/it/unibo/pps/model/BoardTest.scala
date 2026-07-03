package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.utils.Color.Black
import it.unibo.pps.utils.{Color, Position, Shape}

import scala.collection.immutable.HashMap
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, empty, equal, should, shouldBe}

class BoardTest extends AnyFlatSpec:
  val BOARD_SIZE: Int = 8
  val BOARD_SHAPE: Shape = Shape.Square(BOARD_SIZE)
  val DISTANCE: Int = 1

  val left: Int = BOARD_SIZE / 2 - DISTANCE
  val right: Int = left + DISTANCE
  val initialBoard: Board = Board(BOARD_SHAPE, BOARD_SIZE)

  "A Board without disks" should "initialize itself with the disks in the correct positions" in:
    val initialConfiguration: HashMap[Position, Disk] = HashMap(
      Position(left, left) -> Disk(Color.White),
      Position(left, right) -> Disk(Color.Black),
      Position(right, left) -> Disk(Color.Black),
      Position(right, right) -> Disk(Color.White)
    )
    initialBoard.disks should be(initialConfiguration)

  "A Board with disks" should "be created with those disks" in:
    val expectedDisks: HashMap[Position, Disk] = HashMap(
      Position(left, right) -> Disk(Color.Black)
    )
    val board: Board = Board(BOARD_SHAPE, BOARD_SIZE, HashMap(
      Position(left, right) -> Disk(Color.Black)
    ))
    board.disks should be(expectedDisks)
    
  "A Board" should "know which moves are available for a given player" in:
    val configuration: HashMap[Position, Disk] = HashMap(
      Position(left, left) -> Disk(Color.White),
      Position(left, right) -> Disk(Color.Black),
      Position(right, left) -> Disk(Color.Black),
      Position(right, right) -> Disk(Color.White),
      Position(right, right + DISTANCE) -> Disk(Color.White),
      Position(right, right + DISTANCE + DISTANCE) -> Disk(Color.White),
      Position(right, right + DISTANCE + DISTANCE + DISTANCE) -> Disk(Color.White)
    )
    val board: Board = Board(BOARD_SHAPE, BOARD_SIZE, configuration)
    val expectedMovesForBlack: Set[Position] = Set(
      Position(left - DISTANCE, left),
      Position(left, left - DISTANCE),
      Position(right + DISTANCE, right + DISTANCE + DISTANCE),
      Position(right + DISTANCE, right)
    )
    val availableMoves: Set[Position] = board.getAvailableMoves(Color.Black)
    val diffBetweenAvailableAndExpected: Set[Position] = availableMoves diff expectedMovesForBlack
    diffBetweenAvailableAndExpected shouldBe empty
    availableMoves.size should equal (expectedMovesForBlack.size)

  "A Board" should "know if a move is valid" in:
    val validMovePosition: Position = Position(left - DISTANCE, left)
    initialBoard.isMoveValid(validMovePosition, Color.Black) should be(true)

  "A Board" should "know if a move is not valid" in:
    val notValidMovePosition: Position = Position(left - DISTANCE, left- DISTANCE)
    initialBoard.isMoveValid(notValidMovePosition, Color.Black) should be(false)

  "A Board" should "be able to place a new disk in a valid position" in:
    val diskPosition: Position = Position(left - DISTANCE, left)
    val newBoard: Board = initialBoard.placeDisk(diskPosition, Color.Black)
    val expectedDisks: HashMap[Position, Disk] = initialBoard.disks + (diskPosition -> Disk(Color.Black))
    val expectedBoard: Board = Board(BOARD_SHAPE, BOARD_SIZE, expectedDisks)
    newBoard.disks should equal(expectedBoard.disks)
    newBoard.size should equal(expectedBoard.size)
    newBoard.shape should equal(expectedBoard.shape)

  "A Board, if the move is not valid" should "not place the new disk" in:
    val diskPosition: Position = Position(left - DISTANCE, left - DISTANCE)
    val newBoard: Board = initialBoard.placeDisk(diskPosition, Color.Black)
    val expectedDisks: HashMap[Position, Disk] = initialBoard.disks
    val expectedBoard: Board = Board(BOARD_SHAPE, BOARD_SIZE, expectedDisks)
    newBoard.disks should equal(expectedBoard.disks)
    newBoard.size should equal(expectedBoard.size)
    newBoard.shape should equal(expectedBoard.shape)

  "A Board, after placing a disk" should "flip the correct disks" in:
    val diskPosition: Position = Position(left - DISTANCE, left)
    val diskColor: Color = Color.Black
    val configuration: HashMap[Position, Disk] = HashMap(
      Position(left, left) -> Disk(Color.White),
      Position(left, right) -> Disk(Color.White),
      Position(right, left) -> Disk(Color.Black),
      Position(right, right) -> Disk(Color.White),
      Position(left, left - DISTANCE) -> Disk(Color.White),
      Position(left + DISTANCE, left - DISTANCE - DISTANCE) -> Disk(Color.White),
      Position(left + DISTANCE + DISTANCE, left - DISTANCE - DISTANCE - DISTANCE) -> Disk(Color.Black),
      Position(left + DISTANCE, right + DISTANCE) -> Disk(Color.Black),
      Position(left - DISTANCE, right + DISTANCE) -> Disk(Color.Black),
      Position(left - DISTANCE, right) -> Disk(Color.White)
    )
    val board: Board = Board(BOARD_SHAPE, BOARD_SIZE, configuration)
    val boardBeforeFlip: Board = board.placeDisk(diskPosition, diskColor)
    val boardAfterFlip: Board = boardBeforeFlip.flipDisks(diskPosition, diskColor)
    val expectedDisks: HashMap[Position, Disk] = HashMap(
      Position(left, left) -> Disk(Color.Black),
      Position(left, right) -> Disk(Color.Black),
      Position(right, left) -> Disk(Color.Black),
      Position(right, right) -> Disk(Color.White),
      Position(left, left - DISTANCE) -> Disk(Color.Black),
      Position(left + DISTANCE, left - DISTANCE - DISTANCE) -> Disk(Color.Black),
      Position(left + DISTANCE + DISTANCE, left - DISTANCE - DISTANCE - DISTANCE) -> Disk(Color.Black),
      Position(left + DISTANCE, right + DISTANCE) -> Disk(Color.Black),
      Position(left - DISTANCE, right + DISTANCE) -> Disk(Color.Black),
      Position(left - DISTANCE, right) -> Disk(Color.Black),
      diskPosition -> Disk(diskColor)
    )
    val expectedBoard: Board = Board(BOARD_SHAPE, BOARD_SIZE, expectedDisks)
    boardAfterFlip.disks should equal(expectedBoard.disks)
    boardAfterFlip.size should equal(expectedBoard.size)
    boardAfterFlip.shape should equal(expectedBoard.shape)
