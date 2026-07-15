package it.unibo.pps.model.board

import it.unibo.pps.model.board.BoardCreationExtensions.{toPosDiskMap, half}
import it.unibo.pps.state.{BoardState, DiskState}
import it.unibo.pps.utils.{Color, Position, Shape}

trait Board:
  private[board] def shape: Shape
  private[board] def disks: Map[Position, Disk]
  def state: BoardState
  def getAvailablePlacements(diskColor: Color): Set[Position]
  def isPlacementValid(diskColor: Color, diskPos: Position): Boolean
  def placeDisk(diskColor: Color, diskPos: Position): Board
  def captureDisks(diskPos: Position): Board

object Board:
  def apply(shape: Shape): Board =
    val bottomRightCenterPos: Position =
      shape match
        case Shape.Square(n) => (n.half, n.half)
        case Shape.Rectangle(h, w) => (h.half, w.half)

    val initialDisks: Map[Position, Disk] =
      s"""
        ${bottomRightCenterPos.left.up} -> W
        ${bottomRightCenterPos.up} -> B
        ${bottomRightCenterPos.left} -> B
        $bottomRightCenterPos -> W
      """.toPosDiskMap
    
    apply(shape, initialDisks)

  def apply(state: BoardState): Board =
    val disks = state.disks.map(disk => disk.position -> Disk(disk.color)).toMap
    apply(state.shape, disks)
    
  def apply(shape: Shape, disks: Map[Position, Disk]): Board =
    shape match
      case Shape.Square(_) =>
        given contextComputations: PosComputeExtensions = PosComputeExtensions()
        BoardImpl(shape, disks)
      case Shape.Rectangle(_,_) =>
        given contextComputations: PosComputeExtensions = PosComputeExtensionsRectangle()
        BoardImpl(shape, disks)
  
  private class BoardImpl(val shape: Shape, val disks: Map[Position, Disk])
                             (using PosComputeExtensions) extends Board:
    private val compute: BoardComputations = BoardComputations()
    private given contextBoard: Board = this

    val state: BoardState = BoardState(shape, disks.map((pos, disk) => DiskState(disk.color, pos)).toSeq, Set())

    def getAvailablePlacements(diskColor: Color): Set[Position] =
      compute.getAvailablePlacements(diskColor)

    def isPlacementValid(diskColor: Color, diskPos: Position): Boolean =
      compute.isPlacementValid(diskPos, diskColor)

    def placeDisk(diskColor: Color, diskPos: Position): Board =
      compute.placeDisk(diskPos, diskColor)

    def captureDisks(diskPos: Position): Board =
      compute.captureDisks(diskPos)

    override def equals(obj: Any): Boolean =
      obj match
        case b: Board => state.disks.equals(b.state.disks) && shape.equals(b.state.shape)
