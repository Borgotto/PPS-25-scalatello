package it.unibo.pps.model.board

import it.unibo.pps.utils.{Color, Position}

import scala.annotation.tailrec

class BoardComputations(using board: Board)(using PosComputeExtensions):  
  private def getOppositeColorNeighbours(diskColor: Color): Set[(Position, Position)] =
    for diskPos: Position <- board.disks.filter((_, disk) => disk.color.equals(diskColor)).keySet
        possibleNeighbourPos: Position <- board.disks.filter((_, disk) => disk.color.equals(diskColor.opposite)).keySet
        if possibleNeighbourPos.inNeighbourhood(diskPos)
    yield (diskPos, possibleNeighbourPos)

  private def getConnectingDisks(oppositeNeighboursPos: Set[(Position, Position)], 
                                 diskColor: Color, placedDiskPos: Position): Set[Position] =
    @tailrec
    def _getNextConnectingNeighbour(diskPos: Position, placedDisk: (Position, Color), distance: Position): Option[Position] =
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

  private def getDisksToFlip(diskPos: Position): Set[Position] =
    val diskColor: Color = board.disks(diskPos).color
    val oppositeNeighboursPos: Set[(Position, Position)] =
      getOppositeColorNeighbours(diskColor).filter((disk, neighbour) => disk.equals(diskPos))
    for connectingDiskPos: Position <- getConnectingDisks(oppositeNeighboursPos, diskColor, diskPos)
        diskToFlip: Position <- board.disks.keySet
        if diskToFlip.inBetween(diskPos, connectingDiskPos)
    yield diskToFlip

  def getAvailablePlacements(diskColor: Color): Set[Position] =
    @tailrec
    def _getNextEmptyNeighbour(diskPos: Position, distance: Position): Option[Position] =
      val neighbourPos = diskPos - distance
      neighbourPos match
        case p
          if (!p.inBounds(board.shape)) ||
            (board.disks.contains(p) && board.disks(p).color.equals(diskColor)) => Option.empty
        case p if board.disks.contains(p) => _getNextEmptyNeighbour(p, distance)
        case p => Some(p)

    for (disk, neighbour) <- getOppositeColorNeighbours(diskColor)
        diskPos: Position = Position(neighbour.row, neighbour.column)
        distance: Position = disk - neighbour
        availableMove: Option[Position] = _getNextEmptyNeighbour(diskPos, distance)
        if availableMove.isDefined
    yield availableMove.get

  def isPlacementValid(diskPos: Position, diskColor: Color): Boolean =
    getAvailablePlacements(diskColor).contains(diskPos)

  def placeDisk(diskPos: Position, diskColor: Color): Board =
    if !isPlacementValid(diskPos, diskColor) then throw IllegalArgumentException("The placement is not valid")
    Board(board.shape, board.disks + (diskPos -> Disk(diskColor)))

  def captureDisks(diskPos: Position): Board =
    if !board.disks.contains(diskPos) then throw IllegalArgumentException("There isn't a disk in that position")
    val disksToFlip: Set[Position] = getDisksToFlip(diskPos)
    val disksAfterFlip = board.disks.map((pos, disk) => (pos, if disksToFlip.contains(pos) then disk.flip() else disk))
    Board(board.shape, disksAfterFlip)
