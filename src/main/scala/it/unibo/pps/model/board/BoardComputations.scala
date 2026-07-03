package it.unibo.pps.model.board

import it.unibo.pps.state.DiskState
import it.unibo.pps.utils.{Color, Position}

import scala.annotation.tailrec
import scala.collection.immutable
import scala.collection.immutable.HashMap

class BoardComputations:
  private def calculateDirection(distance: Position): Position =
    distance / Position(distance.row.abs, distance.column.abs)

  private def getOppositeColorNeighbours(diskColor: Color, board: Board): Set[(Position, Position)] =
    for diskPos: Position <- board.disks.filter(e => e._2.color.equals(diskColor)).keySet
        possibleNeighbourPos: Position <- board.disks.filter(e => e._2.color.equals(diskColor.opposite)).keySet
        if possibleNeighbourPos.inNeighbourhood(diskPos)
    yield (diskPos, possibleNeighbourPos)

  @tailrec
  private def getNextEmptyNeighbour(diskPos: Position, distance: Position, board: Board): Option[Position] =
    val neighbourPos = Position(diskPos.row - distance.row, diskPos.column - distance.column)
    diskPos match
      case p if !p.neighbourInBoundary(distance, board.shape.width) => Option.empty
      case p if board.disks.contains(neighbourPos) => getNextEmptyNeighbour(neighbourPos, distance, board)
      case _ => Some(neighbourPos)

  private def findAvailableMoves(oppositeNeighboursPos: Set[(Position, Position)], board: Board): Set[Option[Position]] =
    for posPair: (Position, Position) <- oppositeNeighboursPos
        diskPos: Position = Position(posPair._2.row, posPair._2.column)
        distance: Position = posPair._1.distance(posPair._2)
        availableMove: Option[Position] = getNextEmptyNeighbour(diskPos, distance, board)
    yield availableMove

  @tailrec
  private def getNextConnectingNeighbour(diskPos: Position, placedDisk: (Position, Color),
                                         distance: Position, board: Board): Option[Position] =
    val neighbourPos = Position(diskPos.row - distance.row, diskPos.column - distance.column)
    val disksWithoutPlacedDisk: HashMap[Position, Disk] = board.disks.filter(e => !e.equals(placedDisk))
    diskPos match
      case p if !p.neighbourInBoundary(distance, board.shape.width)
        && !board.disks.contains(neighbourPos)
        => Option.empty
      case p if disksWithoutPlacedDisk(neighbourPos).color.equals(placedDisk._2.opposite)
        => getNextConnectingNeighbour(neighbourPos, placedDisk, distance, board)
      case _ => Some(neighbourPos)

  private def getConnectingDisks(oppositeNeighboursPos: Set[(Position, Position)], diskColor: Color,
                                  placedDiskPos: Position, board: Board): Set[Option[Position]] =
    for posPair: (Position, Position) <- oppositeNeighboursPos
        diskPosition: Position = Position(posPair._2.row, posPair._2.column)
        distance: Position = posPair._1.distance(posPair._2)
        connectingDiskPosition: Option[Position]
          = getNextConnectingNeighbour(diskPosition, (placedDiskPos, diskColor), distance, board)
    yield connectingDiskPosition

  private def getPosOnSameDiagonal(firstPos: Position, secondPos: Position,
                                   distance: Position, direction: Position): Seq[Position] =
    val minDistance = 1
    for i <- minDistance to distance.row.abs
        diskPosition = Position(firstPos.row - (direction.row * i), firstPos.column - (direction.column * i))
        if !diskPosition.equals(secondPos)
    yield diskPosition

  private def getDisksToFlip(diskPos: Position, connectingDisksPos: Set[Position], board: Board): Set[Position] =
    for connectingDiskPos: Position <- connectingDisksPos
        distance: Position = diskPos.distance(connectingDiskPos)
        direction: Position = calculateDirection(distance)
        diskToFlip: Position <- board.disks.filter(e => e._1.onSameDiagonal(diskPos, connectingDiskPos, distance, direction)
          || e._1.inBetween(diskPos, connectingDiskPos)).keySet
    yield diskToFlip

  private def getUpdatedDisks(disksToFlip: Set[Position], board: Board): HashMap[Position, Disk] =
    for disk <- board.disks
    yield
      disk match
        case d if disksToFlip.contains(d._1) => d._1 -> d._2.flip()
        case _ => disk

  def getAvailableMoves(diskColor: Color, board: Board): Set[Position] =
    val oppositeNeighboursPos: Set[(Position, Position)] = getOppositeColorNeighbours(diskColor, board)
    val availableMoves: Set[Option[Position]] = findAvailableMoves(oppositeNeighboursPos, board)
    availableMoves.filter(e => e.isDefined).map(e => e.get)

  def isMoveValid(diskPos: Position, diskColor: Color, board: Board): Boolean =
    getAvailableMoves(diskColor, board).contains(diskPos)

  def placeDisk(diskPos: Position, diskColor: Color, board: Board): Board =
    val disk: Disk = Disk(diskColor)
    diskPos match
      case p if isMoveValid(diskPos, diskColor, board)
        => Board(board.shape, board.disks + (diskPos -> disk))
      case _ => board

  def flipDisks(diskPos: Position, diskColor: Color, board: Board): Board =
    val oppositeNeighboursPos: Set[(Position, Position)]
      = getOppositeColorNeighbours(diskColor, board).filter(e => e._1.equals(diskPos))
    val connectingDisksPos: Set[Option[Position]]
      = getConnectingDisks(oppositeNeighboursPos, diskColor, diskPos, board)
    val disksToFlip: Set[Position]
      = getDisksToFlip(diskPos, connectingDisksPos.filter(e => e.isDefined).map(e => e.get), board)
    val flippedDisks: HashMap[Position, Disk] = getUpdatedDisks(disksToFlip, board)
    Board(board.shape, flippedDisks)
    
  def fromMapToSeq(board: Board): Seq[DiskState] =
    val diskStates = 
      for disk <- board.disks
        diskState: DiskState = DiskState(disk._2.color, disk._1)
      yield diskState
    diskStates.toSeq

  extension (x: Int)
    private def inRange(y: Int, z: Int): Boolean =
      (y, z) match
        case (y, z) if y < z => x >= y && x <= z
        case (y, z) if y > z => x >= z && x <= y
        case (_, _) => false

  extension (x: Int)
    private def inBetween(y: Int, z: Int): Boolean =
      (y, z) match
        case (y, z) if y < z => x > y && x < z
        case (y, z) if y > z => x > z && x < y
        case (_, _) => false

  extension (p: Position)
    private def neighbourInBoundary(distance: Position, size: Int): Boolean =
      val minCoordinate = 0
      val maxCoordinate = size - 1
      (p.row - distance.row).inRange(minCoordinate, maxCoordinate)
        && (p.column - distance.column).inRange(minCoordinate, maxCoordinate)

  extension (p: Position)
    private def inBetween(firstPos: Position, secondPos: Position): Boolean =
      (firstPos, secondPos) match
        case (f, s) if f.row.equals(s.row) => p.column.inBetween(f.column, s.column) && p.row.equals(f.row)
        case (f, s) if f.column.equals(s.column) => p.row.inBetween(f.row, s.row) && p.column.equals(f.column)
        case (_, _) => false

  extension (p: Position)
    private def onSameDiagonal(firstPos: Position, secondPos: Position, distance: Position, direction: Position): Boolean =
      distance match
        case d if d.row.abs.equals(distance.column.abs) =>
          val disksOnSameDiagonal: Seq[Position] = getPosOnSameDiagonal(firstPos, secondPos, distance, direction)
          disksOnSameDiagonal.contains(p) && distance.row.abs.equals(distance.column.abs)
        case _ => false

  extension (p: Position)
    private def /(pos: Position): Position =
      (pos.row, pos.column) match
        case (r, c) if r.equals(0) && c.equals(0) => Position(r, c)
        case (r, c) if r.equals(0) => Position(r, p.column / c)
        case (r, c) if c.equals(0) => Position(p.row / r, c)
        case (_, _) => Position(p.row / pos.row, p.column / pos.column)

  extension (p: Position)
    private def distance(pos: Position): Position =
      Position(p.row - pos.row, p.column - pos.column)

  extension (p: Position)
    private def inNeighbourhood(pos: Position): Boolean =
      val maxDistance: Int = 1
      val distance: Position = p.distance(pos)
      distance.row.abs <= maxDistance && distance.column.abs <= maxDistance
