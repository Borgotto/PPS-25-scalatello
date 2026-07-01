package it.unibo.pps.model.board

import scala.collection.immutable.HashMap
import it.unibo.pps.utils.{Color, Position, Shape}
import scala.annotation.tailrec

trait Board:
  def shape: Shape
  def size: Int
  def disks: HashMap[Position, Disk]
  def getAvailableMoves(color: Color): Set[Position] = Board.getAvailableMoves(color, disks, size)
  
object Board:
  def apply(shape: Shape, size: Int): Board =
    val leftCenter = size / 2 - 1
    val rightCenter = leftCenter + 1
    val initialDisks = HashMap(
      Position(leftCenter, leftCenter) -> Disk(Color.White),
      Position(leftCenter, rightCenter) -> Disk(Color.Black),
      Position(rightCenter, leftCenter) -> Disk(Color.Black),
      Position(rightCenter, rightCenter) -> Disk(Color.White)
    )
    new StandardBoard(shape, size, initialDisks)
  
  def apply(shape: Shape, size: Int, disks: HashMap[Position, Disk]): Board = 
    new StandardBoard(shape, size, disks)
    
  def getAvailableMoves(c: Color, disks: HashMap[Position, Disk], size: Int): Set[Position] =
    val neighbourhood = List(-1, 0, 1)
    val oppositeNeighbours =
      for disk <- disks.filter(e => e._2.color.equals(c)).keySet
          possibleNeighbour <- disks.filter(e => e._2.color.equals(c.opposite)).keySet
          if neighbourhood.contains(disk.row - possibleNeighbour.row)
          if neighbourhood.contains(disk.column - possibleNeighbour.column)
      yield (disk, possibleNeighbour)
    val availableMoves =
      for pair: (Position, Position) <- oppositeNeighbours
      yield findEmptyNeighbour(disks, Position(pair._2.row, pair._2.column),
        pair._1.row - pair._2.row, pair._1.column - pair._2.column, size)
    availableMoves.filter(e => e.isDefined).map(e => e.get)

  @tailrec
  private def findEmptyNeighbour(disks: HashMap[Position, Disk], position: Position,
                                  rowStep: Int, columnStep: Int, size: Int): Option[Position] =
    position match
      case p if disks.contains(Position(p.row - rowStep, p.column - columnStep))
        && p.row - rowStep >= 0 && p.row - rowStep < size
        && p.column - columnStep >= 0 && p.column - columnStep < size
      => findEmptyNeighbour(disks, Position(p.row - rowStep, p.column - columnStep), rowStep, columnStep, size)
      case p if !disks.contains(Position(p.row - rowStep, p.column - columnStep))
        && p.row - rowStep >= 0 && p.row - rowStep < size
        && p.column - columnStep >= 0 && p.column - columnStep < size
      => Some(Position(p.row - rowStep, p.column - columnStep))
      case _ => Option.empty
  
  private class StandardBoard(override val shape: Shape, 
                              override val size: Int, 
                              override val disks: HashMap[Position, Disk]) extends Board
