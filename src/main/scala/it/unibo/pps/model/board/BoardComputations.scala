package it.unibo.pps.model.board

import it.unibo.pps.utils.{Color, Position}
import scala.annotation.tailrec
import scala.collection.immutable.HashMap

class BoardComputations:
  def findOppositeNeighbour(color: Color, disks: HashMap[Position, Disk]): Set[(Position, Position)] =
    val neighbourhood = Set(-1, 0, 1)
    for disk <- disks.filter(e => e._2.color.equals(color)).keySet
        possibleNeighbour <- disks.filter(e => e._2.color.equals(color.opposite)).keySet
        rowDistance: Int = disk.row - possibleNeighbour.row
        columnDistance: Int = disk.column - possibleNeighbour.column
        if neighbourhood.contains(rowDistance)
        if neighbourhood.contains(columnDistance)
    yield (disk, possibleNeighbour)

  extension (x: Int)
    private def inRange(y: Int, z: Int): Boolean = x >= y && x <= z

  extension (p: Position)
    private def neighbourInBoundary(rowDistance: Int, columnDistance: Int, size: Int): Boolean =
      val minCoordinate = 0
      val maxCoordinate = size - 1
      (p.row - rowDistance).inRange(minCoordinate, maxCoordinate)
      && (p.column - columnDistance).inRange(minCoordinate, maxCoordinate)

  @tailrec
  private def findEmptyNeighbour(disks: HashMap[Position, Disk], diskPosition: Position,
                                 rowDistance: Int, columnDistance: Int, size: Int): Option[Position] =
    val positionOfNeighbour = Position(diskPosition.row - rowDistance, diskPosition.column - columnDistance)
    diskPosition match
      case p if disks.contains(positionOfNeighbour)
        && p.neighbourInBoundary(rowDistance, columnDistance, size)
        => findEmptyNeighbour(disks, positionOfNeighbour, rowDistance, columnDistance, size)
      case p if !disks.contains(positionOfNeighbour)
        && p.neighbourInBoundary(rowDistance, columnDistance, size)
        => Some(positionOfNeighbour)
      case _ => Option.empty
    
  def findAvailableMoves(oppositeNeighbours: Set[(Position, Position)],
                         disks: HashMap[Position, Disk], size: Int): Set[Option[Position]] =
    for pair: (Position, Position) <- oppositeNeighbours
        diskPosition: Position = Position(pair._2.row, pair._2.column)
        rowDistance: Int = pair._1.row - pair._2.row
        columnDistance: Int = pair._1.column - pair._2.column
        availableMove: Option[Position] = findEmptyNeighbour(disks, diskPosition, rowDistance, columnDistance, size)
    yield availableMove
