package it.unibo.pps.model.board

import it.unibo.pps.state.{BoardState, DiskState}

import it.unibo.pps.utils.{Color, Position, Shape}

trait Board:
  def shape: Shape
  def disks: Map[Position, Disk]
  def state: BoardState
  def getAvailableMoves(diskColor: Color): Set[Position]
  def isMoveValid(diskPos: Position, diskColor: Color): Boolean
  def placeDisk(diskPos: Position, diskColor: Color): Board
  def captureDisks(diskPos: Position): Board
  
object Board:
  def apply(shape: Shape): Board =
    val dist: Int = 1
    val topLeftCenterPos: Position =
      shape match
        case Shape.Square(n) => Position(n / 2 - dist, n / 2 - dist)
        case Shape.Rectangle(h, w) => Position(h / 2 - dist, w / 2 - dist)
    val initialDisks: Map[Position, Disk] = Map(
      Position(topLeftCenterPos.row, topLeftCenterPos.column) -> Disk(Color.White),
      Position(topLeftCenterPos.row, topLeftCenterPos.column + dist) -> Disk(Color.Black),
      Position(topLeftCenterPos.row + dist, topLeftCenterPos.column) -> Disk(Color.Black),
      Position(topLeftCenterPos.row + dist, topLeftCenterPos.column + dist) -> Disk(Color.White)
    )
    apply(shape, initialDisks)
  
  def apply(shape: Shape, disks: Map[Position, Disk]): Board =
    shape match
      case _ => StandardBoard(shape, disks)

  private class StandardBoard(val shape: Shape, val disks: Map[Position, Disk]) extends Board:
    private val compute: BoardComputations = BoardComputations()
    private given contextBoard: Board = this

    val state: BoardState = BoardState(shape, disks.map((pos, disk) => DiskState(disk.color, pos)).toSeq)

    def getAvailableMoves(diskColor: Color): Set[Position] =
      compute.getAvailableMoves(diskColor)

    def isMoveValid(diskPos: Position, diskColor: Color): Boolean =
      compute.isMoveValid(diskPos, diskColor)

    def placeDisk(diskPos: Position, diskColor: Color): Board =
      compute.placeDisk(diskPos, diskColor)

    def captureDisks(diskPos: Position): Board =
      compute.captureDisks(diskPos)

    override def equals(obj: Any): Boolean =
      obj match
        case b: Board => disks.equals(b.disks) && shape.equals(b.shape)
