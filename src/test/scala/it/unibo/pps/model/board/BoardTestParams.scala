package it.unibo.pps.model.board

import it.unibo.pps.domain.{Color, Position, Shape}
import it.unibo.pps.model.board.BoardCreationExtensions.toPosDiskMap
import it.unibo.pps.utils.IntExtensions.half
import it.unibo.pps.testutils.TestExtensions.toBoard
import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.state.{BoardState, DiskState}

/** Helper class to get the params for [[BoardTest]] */
class BoardTestParams(val shape: Shape):
  private val whiteDisk: Disk = Disk(Color.White)
  private val blackDisk: Disk = Disk(Color.Black)
  
  val bottomRightCenterPos: Position =
    shape match
      case Shape.Square(n) => (n.half, n.half)
      case Shape.Rectangle(h, w) => (h.half, w.half)

  val validMovePos: Position =
    shape match
      case _ => bottomRightCenterPos.right.up

  val notValidMovePos: Position =
    shape match
      case _ => bottomRightCenterPos.left.left

  val initialDisksOnBoard: Map[Position, Disk] =
    shape match
      case _ =>
        s"""
          ${bottomRightCenterPos.left.up} -> W
          ${bottomRightCenterPos.up} -> B
          ${bottomRightCenterPos.left} -> B
          $bottomRightCenterPos -> W
        """.toPosDiskMap

  val initialBoard: Board = Board(shape)
  
  val initialBoardState: BoardState = initialBoard.state

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

  val expectedBoardAfterNewDisk: Board =
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

  val expectedDiskStates: Set[DiskState] =
    shape match
      case _ => Set(
        DiskState(whiteDisk.color, bottomRightCenterPos.left.up),
        DiskState(blackDisk.color, bottomRightCenterPos.up),
        DiskState(blackDisk.color, bottomRightCenterPos.left),
        DiskState(whiteDisk.color, bottomRightCenterPos)
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
