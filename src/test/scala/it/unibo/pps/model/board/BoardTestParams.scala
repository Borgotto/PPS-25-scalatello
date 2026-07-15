package it.unibo.pps.model.board

import it.unibo.pps.model.board.BoardCreationExtensions.toPosDiskMap
import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.state.DiskState
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.testutils.TestExtensions.toBoard

class BoardTestParams(val shape: Shape):
  private val DIST: Int = 1
  private val whiteDisk: Disk = Disk(Color.White)
  private val blackDisk: Disk = Disk(Color.Black)
  
  val topLeftCenter: Position =
    shape match
      case Shape.Square(n) => (n / 2 - DIST, n / 2 - DIST)
      case Shape.Rectangle(h, w) => (h / 2 - DIST, w / 2 - DIST)

  val validMovePos: Position =
    shape match
      case _ => (topLeftCenter.row, topLeftCenter.column + DIST + DIST)

  val notValidMovePos: Position =
    shape match
      case _ => (topLeftCenter.row + DIST, topLeftCenter.column - DIST)

  val initialDisksOnBoard: Map[Position, Disk] =
    shape match
      case _ =>
        s"""(${topLeftCenter._1}, ${topLeftCenter._2}) -> W;
        (${topLeftCenter._1}, ${topLeftCenter._2 + DIST}) -> B;
        (${topLeftCenter._1 + DIST}, ${topLeftCenter._2}) -> B;
        (${topLeftCenter._1 + DIST}, ${topLeftCenter._2 + DIST}) -> W;""".toPosDiskMap

  val initialBoard: Board = Board(shape)

  val boardDuringMatch: Board =
    shape match
      case Shape.Square(_) =>
        """
          ....
          BWW.
          .WWW
          .B.B
        """.toBoard
      case Shape.Rectangle(_,_) =>
        """
          ......
          .BWW..
          ..WWW.
          ..B.B.
        """.toBoard

  val expectedAvailableMoves: Set[Position] =
    shape match
      case Shape.Square(_) =>
        """
          .B..
          B...
          ...B
          ..B.
        """.toBoard.disks.keySet
      case Shape.Rectangle(_,_) =>
        """
          ..B...
          .B....
          ....B.
          ...B..
        """.toBoard.disks.keySet

  val expectedBoardAfterCapture: Board =
    shape match
      case Shape.Square(_) =>
        """
          ....
          BBBB
          .WBB
          .B.B
        """.toBoard
      case Shape.Rectangle(_,_) =>
        """
          ......
          .BBBB.
          ..WBB.
          ..B.B.
        """.toBoard

  val expectedDiskStates: Seq[DiskState] =
    shape match
      case _ => Seq(
        DiskState(whiteDisk.color, (topLeftCenter.row, topLeftCenter.column)),
        DiskState(blackDisk.color, (topLeftCenter.row, topLeftCenter.column + DIST)),
        DiskState(blackDisk.color, (topLeftCenter.row + DIST, topLeftCenter.column)),
        DiskState(whiteDisk.color, (topLeftCenter.row + DIST, topLeftCenter.column + DIST))
      )

  val outOfBoundsTestBoard: Board =
    shape match
      case Shape.Square(_) =>
        """
          ....
          WWB.
          .WW.
          ..W.
        """.toBoard
      case Shape.Rectangle(_,_) =>
        """
          ......
          WWWB..
          ..WW..
          ...W..
        """.toBoard

  val expectedNotOutOfBoundsMoves: Set[Position] =
    shape match
      case Shape.Square(_) =>
        """
          ....
          ....
          ....
          B...
        """.toBoard.disks.keySet
      case Shape.Rectangle(_,_) =>
        """
          ......
          ......
          ......
          .B....
        """.toBoard.disks.keySet

  val notAfterSameColorTestBoard: Board =
    shape match
      case Shape.Square(_) =>
        """
          WB..
          .WB.
          .BW.
          ....
        """.toBoard
      case Shape.Rectangle(_,_) =>
        """
          .WB...
          ..WB..
          ..BW..
          ......
        """.toBoard
        
  val expectedNotAfterSameColorMoves: Set[Position] =
    shape match
      case Shape.Square(_) =>
        """
          ....
          B...
          ...B
          ..B.
        """.toBoard.disks.keySet
      case Shape.Rectangle(_,_) =>
        """
          B.....
          .B....
          ....B.
          ...B..
        """.toBoard.disks.keySet
