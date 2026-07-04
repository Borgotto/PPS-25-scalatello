package it.unibo.pps.model.board

import it.unibo.pps.state.{BoardState, DiskState}

import scala.collection.immutable.HashMap
import it.unibo.pps.utils.{Color, Position, Shape}

trait Board:
  def shape: Shape
  def disks: HashMap[Position, Disk]
  def state: BoardState
  def getAvailableMoves(diskColor: Color): Set[Position]
  def isMoveValid(diskPos: Position, diskColor: Color): Boolean
  def placeDisk(diskPos: Position, diskColor: Color): Board
  def flipDisks(diskPos: Position, diskColor: Color): Board
  
object Board:
  def apply(shape: Shape): Board =
    val dist: Int = 1
    shape match
      case Shape.Square(n) =>
        val upLeftCenterPos: Position = Position(n / 2 - dist, n / 2 - dist)
        val initialDisks: HashMap[Position, Disk] = HashMap(
          Position(upLeftCenterPos.row, upLeftCenterPos.column) -> Disk(Color.White),
          Position(upLeftCenterPos.row, upLeftCenterPos.column + dist) -> Disk(Color.Black),
          Position(upLeftCenterPos.row + dist, upLeftCenterPos.column) -> Disk(Color.Black),
          Position(upLeftCenterPos.row + dist, upLeftCenterPos.column + dist) -> Disk(Color.White)
        )
        StandardBoard(shape, initialDisks)
  
  def apply(shape: Shape, disks: HashMap[Position, Disk]): Board = 
    StandardBoard(shape, disks)
  
  private class StandardBoard(override val shape: Shape,
                              override val disks: HashMap[Position, Disk]) extends Board:
    private val compute: BoardComputations = BoardComputations()

    override val state: BoardState =
      val diskState: Seq[DiskState] = compute.fromMapToSeq(this)
      BoardState(shape, diskState)

    override def getAvailableMoves(diskColor: Color): Set[Position] =
      compute.getAvailableMoves(diskColor, this)

    override def isMoveValid(diskPos: Position, diskColor: Color): Boolean =
      compute.isMoveValid(diskPos, diskColor, this)

    override def placeDisk(diskPos: Position, diskColor: Color): Board =
      compute.placeDisk(diskPos, diskColor, this)

    override def flipDisks(diskPos: Position, diskColor: Color): Board =
      compute.flipDisks(diskPos, diskColor, this)

    override def equals(obj: Any): Boolean =
      obj match
        case o: Board => disks.equals(o.disks) && shape.equals(o.shape)
