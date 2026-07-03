package it.unibo.pps.model.board

import scala.collection.immutable.HashMap
import it.unibo.pps.utils.{Color, Position, Shape}

trait Board:
  def shape: Shape
  def size: Int
  def disks: HashMap[Position, Disk]
  def getAvailableMoves(color: Color): Set[Position]
  def isMoveValid(diskPosition: Position, color: Color): Boolean
  def placeDisk(diskPosition: Position, color: Color): Board
  def flipDisks(diskPosition: Position, color: Color): Board
  
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
    
    override def getAvailableMoves(color: Color): Set[Position] =
      compute.getAvailableMoves(color, this)

    override def isMoveValid(diskPosition: Position, color: Color): Boolean =
      compute.isMoveValid(diskPosition, color, this)

    override def placeDisk(diskPosition: Position, color: Color): Board =
      compute.placeDisk(diskPosition, color, this)

    override def flipDisks(diskPosition: Position, color: Color): Board =
      compute.flipDisks(diskPosition, color, this)

    override def equals(obj: Any): Boolean =
      obj match
        case o: Board => disks.equals(o.disks) && size.equals(o.size) && shape.equals(o.shape)
