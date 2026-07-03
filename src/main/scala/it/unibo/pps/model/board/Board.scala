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
      val oppositeNeighbours: Set[(Position, Position)] = compute.findOppositeNeighbour(color, disks)
      val availableMoves: Set[Option[Position]] = compute.findAvailableMoves(oppositeNeighbours, disks, size)
      availableMoves.filter(e => e.isDefined).map(e => e.get)

    override def isMoveValid(diskPosition: Position, color: Color): Boolean =
      getAvailableMoves(color).contains(diskPosition)

    override def placeDisk(diskPosition: Position, color: Color): Board =
      val disk: Disk = Disk(color)
      diskPosition match
        case p if isMoveValid(diskPosition, color) => Board(shape, size, disks + (diskPosition -> disk))
        case _ => this

    override def flipDisks(diskPosition: Position, color: Color): Board =
      val oppositeNeighbours: Set[(Position, Position)] = compute.findOppositeNeighbour(color, disks)
        .filter(e => e._1.row.equals(diskPosition.row) && e._1.column.equals(diskPosition.column))
      val connectingDisks: Set[Option[Position]] = compute.findConnectingDisks(oppositeNeighbours, disks, size, color, diskPosition)
      val disksToFlip: Set[Position] = compute.getDisksToFlip(diskPosition, 
        connectingDisks.filter(e => e.isDefined).map(e => e.get), disks)
      val flippedDisks: HashMap[Position, Disk] = compute.getUpdatedDisks(disksToFlip, disks)
      Board(shape, size, flippedDisks)
