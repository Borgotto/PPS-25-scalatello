package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.state.{BoardState, DiskState}
import it.unibo.pps.utils.{Color, Position, Shape}

import scala.collection.immutable.HashMap
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class RectangleBoardTest extends AnyFlatSpec:
  private val BOARD_HEIGHT: Int = 4
  private val BOARD_WIDTH: Int = 6
  private val BOARD_SHAPE = Shape.Rectangle(BOARD_HEIGHT, BOARD_WIDTH)
  private val DIST: Int = 1

  private val whiteDisk = Disk(Color.White)
  private val blackDisk = Disk(Color.Black)
  private val topLeftCenterPos = Position(BOARD_HEIGHT / 2 - DIST, BOARD_WIDTH / 2 - DIST)
  private val initialDisksOnBoard: HashMap[Position, Disk] = HashMap(
    Position(topLeftCenterPos.row, topLeftCenterPos.column) -> whiteDisk,
    Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> blackDisk,
    Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column) -> blackDisk,
    Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST) -> whiteDisk
  )
  private val boardDuringGame = Board(BOARD_SHAPE, initialDisksOnBoard ++ HashMap(
    Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> whiteDisk,
    Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column) -> whiteDisk,
    Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST) -> blackDisk,
    Position(topLeftCenterPos.row, topLeftCenterPos.column) -> whiteDisk,
    Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column) -> blackDisk,
    Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST) -> whiteDisk,
    Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST + DIST) -> blackDisk
  ))
  private val initialBoard: Board = Board(BOARD_SHAPE)
  private val validMovePosition: Position = Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST + DIST)
  private val notValidMovePosition: Position = Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column - DIST)

  "A Rectangle Board without disks" should "initialize itself with the disks in the correct positions" in :
    initialBoard.disks should be(initialDisksOnBoard)

  "A Rectangle Board with disks" should "be created with those disks" in :
    initialBoard.disks should be(Board(BOARD_SHAPE, initialDisksOnBoard).disks)

  "A Rectangle Board" should "know which moves are available for a given player" in :
    val expectedMoves: Set[Position] = Set(
      Position(topLeftCenterPos.row - DIST, topLeftCenterPos.column),
      Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST),
      Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST),
      Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST),
    )
    val availableMoves: Set[Position] = initialBoard.getAvailableMoves(Color.Black)
    availableMoves.equals(expectedMoves) should be(true)

  "A Rectangle Board" should "know if a move is valid" in :
    boardDuringGame.isMoveValid(validMovePosition, Color.Black) should be(true)

  "A Rectangle Board" should "know if a move is not valid" in :
    initialBoard.isMoveValid(notValidMovePosition, Color.Black) should be(false)

  "A Rectangle Board" should "be able to place a new disk in a valid position" in :
    val board: Board = boardDuringGame.placeDisk(validMovePosition, Color.Black)
    val expectedBoard: Board = Board(BOARD_SHAPE, boardDuringGame.disks + (validMovePosition -> blackDisk))
    board.equals(expectedBoard) should be(true)

  "A Rectangle Board, if the move is not valid" should "not place the new disk" in :
    val newRectangleBoard: Board = boardDuringGame.placeDisk(notValidMovePosition, Color.Black)
    newRectangleBoard.equals(boardDuringGame) should be(true)

  "A Rectangle Board, after placing a disk" should "capture the correct disks" in :
    val board: Board = boardDuringGame.placeDisk(validMovePosition, Color.Black).captureDisks(validMovePosition, Color.Black)
    val expectedBoard: Board = Board(BOARD_SHAPE, boardDuringGame.disks ++ HashMap(
      Position(topLeftCenterPos.row, topLeftCenterPos.column) -> blackDisk,
      Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> blackDisk,
      Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST) -> blackDisk,
      Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST) -> blackDisk,
      validMovePosition -> blackDisk
    ))
    board.equals(expectedBoard) should be(true)

  "A Rectangle Board" should "know if is equal to another board" in :
    initialBoard.equals(initialBoard) should be(true)

  "A Rectangle Board" should "return its correct state" in :
    val boardState: BoardState = initialBoard.state
    val diskStates: Seq[DiskState] = Seq(
      DiskState(whiteDisk.color, Position(topLeftCenterPos.row, topLeftCenterPos.column)),
      DiskState(blackDisk.color, Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST)),
      DiskState(blackDisk.color, Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column)),
      DiskState(whiteDisk.color, Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST))
    )
    val expectedState: BoardState = BoardState(BOARD_SHAPE, diskStates)
    boardState.shape.equals(expectedState.shape) should be(true)
    boardState.disks.toSet.equals(expectedState.disks.toSet) should be(true)
