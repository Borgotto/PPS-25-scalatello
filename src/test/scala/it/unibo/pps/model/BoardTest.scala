package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.state.{BoardState, DiskState}
import it.unibo.pps.utils.{Color, Position, Shape}

import scala.collection.immutable.HashMap
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class BoardTest(shape: Shape) extends AnyFlatSpec:
  private val DIST: Int = 1

  private val whiteDisk: Disk = Disk(Color.White)
  private val blackDisk: Disk = Disk(Color.Black)
  private val topLeftCenterPos: Position =
    shape match
      case Shape.Square(n) => Position(n / 2 - DIST, n / 2 - DIST)
      case Shape.Rectangle(h, w) => Position(h / 2 - DIST, w / 2 - DIST)
  private val initialDisksOnBoard: HashMap[Position, Disk] =
    shape match
      case _ => HashMap(
        Position(topLeftCenterPos.row, topLeftCenterPos.column) -> whiteDisk,
        Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> blackDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column) -> blackDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST) -> whiteDisk
      )
  private val boardDuringGame: Board =
    shape match
      case _ => Board(shape, initialDisksOnBoard ++ HashMap(
        Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> whiteDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column) -> whiteDisk,
        Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST) -> blackDisk,
        Position(topLeftCenterPos.row, topLeftCenterPos.column) -> whiteDisk,
        Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column) -> blackDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST) -> whiteDisk,
        Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST + DIST) -> blackDisk
      ))
  private val initialBoard: Board = Board(shape)
  private val validMovePos: Position =
    shape match
      case _ => Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST + DIST)
  private val notValidMovePos: Position =
    shape match
      case _ => Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column - DIST)

  def creationInitialBoardTest(): Unit =
    initialBoard.disks should be(initialDisksOnBoard)
    
  def creationBoardWithDisksTest(): Unit =
    initialBoard.disks should be(Board(shape, initialDisksOnBoard).disks)
    
  def availableMovesTest(): Unit =
    val expectedMoves: Set[Position] = 
      shape match
        case _ => Set(
          Position(topLeftCenterPos.row - DIST, topLeftCenterPos.column),
          Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST),
          Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST),
          Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST),
        )
    val availableMoves: Set[Position] = initialBoard.getAvailableMoves(Color.Black)
    availableMoves.equals(expectedMoves) should be(true)
    
  def validMoveTest(): Unit =
    boardDuringGame.isMoveValid(validMovePos, Color.Black) should be(true)
    
  def notValidMoveTest(): Unit =
    boardDuringGame.isMoveValid(notValidMovePos, Color.Black) should be(false)
    
  def equalsTest(): Unit =
    initialBoard.equals(initialBoard) should be(true)
    
  def placeDiskInValidMoveTest(): Unit =
    val board: Board = boardDuringGame.placeDisk(validMovePos, Color.Black)
    val expectedBoard: Board = Board(shape, boardDuringGame.disks + (validMovePos -> blackDisk))
    board.equals(expectedBoard) should be(true)
    
  def placeDiskInNotValidMoveTest(): Unit =
    val board: Board = boardDuringGame.placeDisk(notValidMovePos, Color.Black)
    board.equals(boardDuringGame) should be(true)
    
  def captureDisksTest(): Unit =
    val board: Board = boardDuringGame.placeDisk(validMovePos, Color.Black).captureDisks(validMovePos, Color.Black)
    val expectedBoard: Board = 
      shape match
        case _ => Board(shape, boardDuringGame.disks ++ HashMap(
          Position(topLeftCenterPos.row, topLeftCenterPos.column) -> blackDisk,
          Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> blackDisk,
          Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST) -> blackDisk,
          Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST) -> blackDisk,
          validMovePos -> blackDisk
        ))
    board.equals(expectedBoard) should be(true)
    
  def boardStateTest(): Unit =
    val boardState: BoardState = initialBoard.state
    val diskStates: Seq[DiskState] = 
      shape match
        case _ => Seq(
          DiskState(whiteDisk.color, Position(topLeftCenterPos.row, topLeftCenterPos.column)),
          DiskState(blackDisk.color, Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST)),
          DiskState(blackDisk.color, Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column)),
          DiskState(whiteDisk.color, Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST))
        )
    val expectedState: BoardState = BoardState(shape, diskStates)
    boardState.shape.equals(expectedState.shape) should be(true)
    boardState.disks.toSet.equals(expectedState.disks.toSet) should be(true)
