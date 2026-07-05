package it.unibo.pps.model

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.state.DiskState
import it.unibo.pps.utils.{Color, Position, Shape}

class BoardTestParams(val shape: Shape):
  private val DIST: Int = 1
  private val whiteDisk: Disk = Disk(Color.White)
  private val blackDisk: Disk = Disk(Color.Black)
  
  val topLeftCenterPos: Position =
    shape match
      case Shape.Square(n) => Position(n / 2 - DIST, n / 2 - DIST)
      case Shape.Rectangle(h, w) => Position(h / 2 - DIST, w / 2 - DIST)
  val initialDisksOnBoard: Map[Position, Disk] =
    shape match
      case _ => Map(
        Position(topLeftCenterPos.row, topLeftCenterPos.column) -> whiteDisk,
        Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> blackDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column) -> blackDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST) -> whiteDisk
      )
  val boardDuringMatch: Board =
    shape match
      case _ => Board(shape, initialDisksOnBoard ++ Map(
        Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> whiteDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column) -> whiteDisk,
        Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST) -> blackDisk,
        Position(topLeftCenterPos.row, topLeftCenterPos.column) -> whiteDisk,
        Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column) -> blackDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST) -> whiteDisk,
        Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST + DIST) -> blackDisk
      ))
  val initialBoard: Board = Board(shape)
  val validMovePos: Position =
    shape match
      case _ => Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST + DIST)
  val notValidMovePos: Position =
    shape match
      case _ => Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column - DIST)
  val expectedAvailableMoves: Set[Position] =
    shape match
      case _ => Set(
        Position(topLeftCenterPos.row - DIST, topLeftCenterPos.column),
        Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST),
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST),
        Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST),
      )
  val expectedBoardAfterCapture: Board =
    shape match
      case _ => Board(shape, boardDuringMatch.disks ++ Map(
        Position(topLeftCenterPos.row, topLeftCenterPos.column) -> blackDisk,
        Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST) -> blackDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST) -> blackDisk,
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST) -> blackDisk,
        validMovePos -> blackDisk
      ))
  val expectedDiskStates: Seq[DiskState] =
    shape match
      case _ => Seq(
        DiskState(whiteDisk.color, Position(topLeftCenterPos.row, topLeftCenterPos.column)),
        DiskState(blackDisk.color, Position(topLeftCenterPos.row, topLeftCenterPos.column + DIST)),
        DiskState(blackDisk.color, Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column)),
        DiskState(whiteDisk.color, Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST))
      )
  val outOfBoundsTestBoard: Board =
    shape match
      case Shape.Square(n) => Board(shape, initialDisksOnBoard ++ Map(
          Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST) -> whiteDisk,
          Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column) -> whiteDisk,
          Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST) -> whiteDisk
        ))
      case Shape.Rectangle(h, w) => Board(shape, initialDisksOnBoard ++ Map(
          Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST) -> whiteDisk,
          Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST - DIST) -> whiteDisk,
          Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column) -> whiteDisk,
          Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST) -> whiteDisk
        ))
  val expectedNotOutOfBoundsMoves: Set[Position] =
    shape match
      case _ => Set(Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column - DIST))
