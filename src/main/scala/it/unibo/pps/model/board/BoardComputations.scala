package it.unibo.pps.model.board

import it.unibo.pps.utils.{Color, Position, Shape}

import scala.annotation.tailrec
import scala.math.Ordering.Int

class BoardComputations:
  private def getOppositeColorNeighbours(diskColor: Color)(using board: Board): Set[(Position, Position)] =
    for diskPos: Position <- board.disks.filter((_, disk) => disk.color.equals(diskColor)).keySet
        possibleNeighbourPos: Position <- board.disks.filter((_, disk) => disk.color.equals(diskColor.opposite)).keySet
        if possibleNeighbourPos.inNeighbourhood(diskPos)
    yield (diskPos, possibleNeighbourPos)

  private def getConnectingDisks(oppositeNeighboursPos: Set[(Position, Position)], diskColor: Color,
                                 placedDiskPos: Position)(using board: Board): Set[Position] =
    @tailrec
    def _getNextConnectingNeighbour(diskPos: Position, placedDisk: (Position, Color),
                                    distance: Position)(using board: Board): Option[Position] =
      val neighbourPos = diskPos - distance
      val disksWithoutPlacedDisk: Map[Position, Disk] = board.disks.filter(e => !e.equals(placedDisk))
      neighbourPos match
        case p if !p.inBounds(board.shape) || !board.disks.contains(p) => Option.empty
        case p if disksWithoutPlacedDisk(p).color.equals(placedDisk._2.opposite) =>
          _getNextConnectingNeighbour(p, placedDisk, distance)
        case p => Some(p)
    for (disk, neighbour) <- oppositeNeighboursPos
        diskPosition: Position = Position(neighbour.row, neighbour.column)
        direction: Position = disk - neighbour
        connectingDiskPosition: Option[Position] =
          _getNextConnectingNeighbour(diskPosition, (placedDiskPos, diskColor), direction)
        if connectingDiskPosition.isDefined
    yield connectingDiskPosition.get

  @tailrec
  private def getPosOnSameDiagonal(source: Position, destination: Position,
                                   direction: Position, acc: Set[Position] = Set()): Set[Position] =
    val nextPos: Position = source - direction
    (nextPos, destination) match
      case (f, s) if f.equals(s) => acc
      case (f, s) => getPosOnSameDiagonal(f , s, direction, acc + f)

  private def getDisksToFlip(diskPos: Position)(using board: Board): Set[Position] =
    val diskColor: Color = board.disks(diskPos).color
    val oppositeNeighboursPos: Set[(Position, Position)] =
      getOppositeColorNeighbours(diskColor).filter((disk, neighbour) => disk.equals(diskPos))
    for connectingDiskPos: Position <- getConnectingDisks(oppositeNeighboursPos, diskColor, diskPos)
        diskToFlip: Position <- board.disks.keySet
        if diskToFlip.inBetween(diskPos, connectingDiskPos)
    yield diskToFlip

  def getAvailablePlacements(diskColor: Color)(using board: Board): Set[Position] =
    @tailrec
    def _getNextEmptyNeighbour(diskPos: Position, distance: Position)(using board: Board): Option[Position] =
      val neighbourPos = diskPos - distance
      neighbourPos match
        case p if !p.inBounds(board.shape) => Option.empty
        case p if board.disks.contains(p) => _getNextEmptyNeighbour(p, distance)
        case p => Some(p)
    for (disk, neighbour) <- getOppositeColorNeighbours(diskColor)
        diskPos: Position = Position(neighbour.row, neighbour.column)
        distance: Position = disk - neighbour
        availableMove: Option[Position] = _getNextEmptyNeighbour(diskPos, distance)
        if availableMove.isDefined
    yield availableMove.get

  def isPlacementValid(diskPos: Position, diskColor: Color)(using board: Board): Boolean =
    getAvailablePlacements(diskColor).contains(diskPos)

  def placeDisk(diskPos: Position, diskColor: Color)(using board: Board): Board =
    if !isPlacementValid(diskPos, diskColor) then throw IllegalArgumentException("The placement is not valid")
    Board(board.shape, board.disks + (diskPos -> Disk(diskColor)))

  def captureDisks(diskPos: Position)(using board: Board): Board =
    if !board.disks.contains(diskPos) then throw IllegalArgumentException("There isn't a disk in that position")
    val disksToFlip: Set[Position] = getDisksToFlip(diskPos)
    val disksAfterFlip = board.disks.map((pos, disk) => (pos, if disksToFlip.contains(pos) then disk.flip() else disk))
    Board(board.shape, disksAfterFlip)
    
  extension (x: Int)
    private def inRange(y: Int, z: Int): Boolean =
      x <= Int.max(y, z) && x >= Int.min(y, z)

  extension (x: Int)
    private def inBetween(y: Int, z: Int): Boolean =
      x.inRange(Int.min(y, z) + 1, Int.max(y, z) - 1)

  extension (p: Position)
    private def inBounds(shape: Shape): Boolean =
      val minPosition: Position = Position(0, 0)
      shape match
        case Shape.Square(size) =>
          p.row.inRange(minPosition.row, size - 1) &&
          p.column.inRange(minPosition.column, size - 1)
        case Shape.Rectangle(height, width) =>
          p.row.inRange(minPosition.row, height - 1) &&
          p.column.inRange(minPosition.column, width - 1)

  extension (p: Position)
    private def inBetween(firstPos: Position, secondPos: Position): Boolean =
      (firstPos, secondPos) match
        case (f, s) if f.row.equals(s.row) => p.column.inBetween(f.column, s.column) && p.row.equals(f.row)
        case (f, s) if f.column.equals(s.column) => p.row.inBetween(f.row, s.row) && p.column.equals(f.column)
        case (f, s) => p.onSameDiagonal(f, s)

  extension (p: Position)
    private def onSameDiagonal(firstPos: Position, secondPos: Position): Boolean =
      val distance: Position = firstPos - secondPos
      val direction: Position = distance / Position(distance.row.abs, distance.column.abs)
      getPosOnSameDiagonal(firstPos, secondPos, direction).contains(p) &&
      distance.row.abs.equals(distance.column.abs)

  extension (p: Position)
    private def /(pos: Position): Position =
      (pos.row, pos.column) match
        case (r, c) if r.equals(0) && c.equals(0) => Position(r, c)
        case (r, c) if r.equals(0) => Position(r, p.column / c)
        case (r, c) if c.equals(0) => Position(p.row / r, c)
        case (_, _) => Position(p.row / pos.row, p.column / pos.column)

  extension (p: Position)
    private def -(pos: Position): Position =
      Position(p.row - pos.row, p.column - pos.column)

  extension (p: Position)
    private def inNeighbourhood(pos: Position): Boolean =
      val maxDistance: Int = 1
      val distance: Position = p - pos
      distance.row.abs <= maxDistance && distance.column.abs <= maxDistance
