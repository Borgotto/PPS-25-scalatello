package it.unibo.pps.model.board

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.state.DiskState
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.testutils.TestExtensions.toBoard

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
  val initialBoard: Board = Board(shape)
  val boardDuringMatch: Board =
    shape match
      case Shape.Square(_) => """
        ....
        BWW.
        .WWW
        .B.B
      """.toBoard
      case Shape.Rectangle(_,_) => """
|       ......
|       .BWW..
|       ..WWW.
|       ..B.B.
|     """.toBoard
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
      case Shape.Square(_) => """
        ....
        BBBB
        .WBB
        .B.B
      """.toBoard
      case Shape.Rectangle(_,_) => """
|       ......
|       .BBBB.
|       ..WBB.
|       ..B.B.
|     """.toBoard
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
      case Shape.Square(_) => """
        ....
        WWB.
        .WW.
        ..W.
      """.toBoard
      case Shape.Rectangle(_,_) => """
 |      ......
 |      WWWB..
 |      ..WW..
 |      ...W..
 |    """.toBoard
  val expectedNotOutOfBoundsMoves: Set[Position] =
    shape match
      case _ => Set(Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column - DIST))
  val notAfterSameColorTestBoard: Board =
    shape match
      case Shape.Square(_) => """
        WB..
        .WB.
        .BW.
        ....
      """.toBoard
      case Shape.Rectangle(_,_) => """
 |      .WB...
 |      ..WB..
 |      ..BW..
 |      ......
 |    """.toBoard
  val expectedNotAfterSameColorMoves: Set[Position] =
    shape match
      case Shape.Square(_) => Set(
        Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST),
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST),
        Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST)
      )
      case Shape.Rectangle(_, _) => Set(
        Position(topLeftCenterPos.row - DIST, topLeftCenterPos.column - DIST - DIST),
        Position(topLeftCenterPos.row, topLeftCenterPos.column - DIST),
        Position(topLeftCenterPos.row + DIST, topLeftCenterPos.column + DIST + DIST),
        Position(topLeftCenterPos.row + DIST + DIST, topLeftCenterPos.column + DIST)
      )
