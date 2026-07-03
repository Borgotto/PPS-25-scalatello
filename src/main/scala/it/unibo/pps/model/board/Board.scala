package it.unibo.pps.model.board

import it.unibo.pps.state.{BoardState, DiskState}

import scala.collection.immutable.HashMap
import it.unibo.pps.utils.{Color, Position, Shape}

trait Board:
  def shape: Shape
  def size: Int
  def disks: HashMap[Position, Disk]
  def state: BoardState
  def getAvailableMoves(diskColor: Color): Set[Position]
  def isMoveValid(diskPos: Position, diskColor: Color): Boolean
  def placeDisk(diskPos: Position, diskColor: Color): Board
  def flipDisks(diskPos: Position, diskColor: Color): Board
  
object Board:
  def apply(shape: Shape, size: Int): Board =
    val distance: Int = 1;
    val left: Int = size / 2 - distance
    val right: Int = left + distance
    val initialDisks: HashMap[Position, Disk] = HashMap(
      Position(left, left) -> Disk(Color.White),
      Position(left, right) -> Disk(Color.Black),
      Position(right, left) -> Disk(Color.Black),
      Position(right, right) -> Disk(Color.White)
    )
    StandardBoard(shape, size, initialDisks)
  
  def apply(shape: Shape, size: Int, disks: HashMap[Position, Disk]): Board = 
    StandardBoard(shape, size, disks)
  
  private class StandardBoard(override val shape: Shape,
                              override val size: Int, 
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
        case o: Board => disks.equals(o.disks) && size.equals(o.size) && shape.equals(o.shape)
