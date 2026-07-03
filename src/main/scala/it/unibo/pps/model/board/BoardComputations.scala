package it.unibo.pps.model.board

import it.unibo.pps.utils.Color.Black
import it.unibo.pps.utils.{Color, Position}

import scala.annotation.tailrec
import scala.collection.immutable
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

  @tailrec
  private def findConnectingNeighbour(disks: HashMap[Position, Disk], diskPosition: Position,
                                      rowDistance: Int, columnDistance: Int, size: Int, color: Color,
                                      placedDiskPosition: Position): Option[Position] =
    val positionOfNeighbour = Position(diskPosition.row - rowDistance, diskPosition.column - columnDistance)
    diskPosition match
      case p if disks.contains(positionOfNeighbour)
        && p.neighbourInBoundary(rowDistance, columnDistance, size)
        && disks(positionOfNeighbour).color.equals(color.opposite)
      => findConnectingNeighbour(disks, positionOfNeighbour, rowDistance, columnDistance, size, color, placedDiskPosition)
      case p if disks.contains(positionOfNeighbour)
        && p.neighbourInBoundary(rowDistance, columnDistance, size)
        && disks(positionOfNeighbour).color.equals(color)
        && !positionOfNeighbour.equals(placedDiskPosition)
      => Some(positionOfNeighbour)
      case _ => Option.empty

  def findConnectingDisks(oppositeNeighbours: Set[(Position, Position)],
                      disks: HashMap[Position, Disk], size: Int, color: Color, placedDiskPosition: Position): Set[Option[Position]] =
    for pair: (Position, Position) <- oppositeNeighbours
        diskPosition: Position = Position(pair._2.row, pair._2.column)
        rowDistance: Int = pair._1.row - pair._2.row
        columnDistance: Int = pair._1.column - pair._2.column
        connectingDiskPosition: Option[Position] = findConnectingNeighbour(disks, diskPosition,
          rowDistance, columnDistance, size, color, placedDiskPosition)
    yield connectingDiskPosition

  extension (x: Int)
    private def inBetween(y: Int, z: Int): Boolean =
      (y, z) match
        case (y, z) if y < z => x > y && x < z
        case (y, z) if y > z => x > z && x < y
        case (_, _) => false

  extension (p: Position)
    private def inBetween(firstPos: Position, secondPos: Position): Boolean =
      (firstPos, secondPos) match
        case (f, s) if f.row.equals(s.row) => p.column.inBetween(f.column, s.column) && p.row.equals(f.row)
        case (f, s) if f.column.equals(s.column) => p.row.inBetween(f.row, s.row) && p.column.equals(f.column)
        case (_, _) => false

  private def findPosOnSameDiagonal(firstPos: Position, secondPos: Position, distance: Position, direction: Position): Seq[Position] =
    val minDistance = 1
    for i <- minDistance to distance.row.abs
        diskPosition = Position(firstPos.row - (direction.row * i), firstPos.column - (direction.column * i))
        if !diskPosition.equals(secondPos)
    yield diskPosition

  extension (p: Position)
    private def onSameDiagonal(firstPos: Position, secondPos: Position, distance: Position, direction: Position): Boolean =
      distance match
        case d if d.row.abs.equals(distance.column.abs) =>
          val disksOnSameDiagonal: Seq[Position] = findPosOnSameDiagonal(firstPos, secondPos, distance, direction)
          disksOnSameDiagonal.contains(p) && distance.row.abs.equals(distance.column.abs)
        case _ => false

  extension (p: Position)
    private def /(pos: Position): Position =
      (pos.row, pos.column) match
        case (r, c) if r.equals(0) && c.equals(0) => Position(r, c)
        case (r, c) if r.equals(0) => Position(r, p.column / c)
        case (r, c) if c.equals(0) => Position(p.row / r, c)
        case (_, _) => Position(p.row / pos.row, p.column / pos.column)

  def getDisksToFlip(diskPosition: Position, connectingDisks: Set[Position], disks: HashMap[Position, Disk]): Set[Position] =
    for connectingDisk: Position <- connectingDisks
        distance: Position = Position(diskPosition.row - connectingDisk.row, diskPosition.column - connectingDisk.column)
        direction: Position = distance / Position(distance.row.abs, distance.column.abs)
        diskToFlip: Position <- disks.filter(e => e._1.onSameDiagonal(diskPosition, connectingDisk, distance, direction)
          || e._1.inBetween(diskPosition, connectingDisk)).keySet
    yield diskToFlip

  def getUpdatedDisks(disksToFlip: Set[Position], disks: HashMap[Position, Disk]): HashMap[Position, Disk] =
    for disk <- disks
    yield
      disk match
        case d if disksToFlip.contains(d._1) => d._1 -> d._2.flip()
        case _ => disk
