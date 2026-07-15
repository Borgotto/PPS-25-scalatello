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
    val dist: Int = 1
    val topLeftCenter =
      shape match
        case Shape.Square(n) => (n.half - dist, n.half - dist)
        case Shape.Rectangle(h, w) => (h.half - dist, w.half - dist)

    val initialDisks = s"""(${topLeftCenter._1}, ${topLeftCenter._2}) -> W;
        (${topLeftCenter._1}, ${topLeftCenter._2 + dist}) -> B;
        (${topLeftCenter._1 + dist}, ${topLeftCenter._2}) -> B;
        (${topLeftCenter._1 + dist}, ${topLeftCenter._2 + dist}) -> W;""".toPosDiskMap
    
    apply(shape, initialDisks)

  def apply(state: BoardState): Board =
    val disks = state.disks.map(disk => disk.position -> Disk(disk.color)).toMap
    apply(state.shape, disks)
    
  def apply(shape: Shape, disks: Map[Position, Disk]): Board =
    shape match
      case Shape.Square(_) =>
        given contextComputations: PosComputeExtensions = PosComputeExtensions()
        StandardBoard(shape, disks)
      case Shape.Rectangle(_,_) =>
        given contextComputations: PosComputeExtensions = PosComputeExtensionsRectangle()
        RectangleBoard(shape, disks)
  
  private class StandardBoard(val shape: Shape, val disks: Map[Position, Disk])
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

  private class RectangleBoard(override val shape: Shape, override val disks: Map[Position, Disk])
                              (using PosComputeExtensions) extends StandardBoard(shape, disks)