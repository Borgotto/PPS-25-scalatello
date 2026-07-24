package it.unibo.pps.model.board.computations

import it.unibo.pps.domain.{Color, Position}
import it.unibo.pps.model.board.{Board, Disk}

import scala.annotation.tailrec

/** The delegate class of [[BoardImpl]].
 *
 *  This helper class computes all the methods of its delegator.
 *
 *  @param board           the [[Board]] context, to make this class operate on the correct instance of board.
 *  @param computations the [[ComputationsPosExtensions]] context,
 *                         to make this class use the appropriate methods on different Boards.
 */
private[board] class BoardComputations(using board: Board)(using computations: ComputationsPosExtensions):
  private def getCapturableNeighboursPair(diskColor: Color)(using disks: Map[Position, Disk]): Set[(Position, Position)] =
    val sameColorDisks: Set[Position] = disks.filter((_, disk) => disk.color.equals(diskColor)).keySet
    val capturableDisks: Set[Position] = disks.keySet -- sameColorDisks
    for
      diskPos: Position <- sameColorDisks
      possibleNeighbourPos: Position <- capturableDisks
      if possibleNeighbourPos.inNeighbourhood(diskPos)
    yield (diskPos, possibleNeighbourPos)

  private def getConnectingDisks(diskColor: Color, placedDiskPos: Position)
                                (using disks: Map[Position, Disk]): Set[Position] =
    @tailrec
    def _getNextConnectingDisk(diskPos: Position, placedDisk: (Position, Color), direction: Position): Option[Position] =
      val neighbourPos = diskPos - direction
      neighbourPos match
        case p if !p.inBounds(board.shape) || !board.disks.contains(p) => Option.empty
        case p if board.disks(p).color.equals(placedDisk._2.opposite) =>
          _getNextConnectingDisk(p, placedDisk, direction)
        case p => Some(p)

    val capturableNeighboursPos: Set[(Position, Position)] =
      getCapturableNeighboursPair(diskColor).filter((disk, _) => disk.equals(placedDiskPos))
    for
      (disk, neighbour) <- capturableNeighboursPos
      direction: Position = disk - neighbour
      connectingDiskPosition: Option[Position] =
        _getNextConnectingDisk(neighbour, (placedDiskPos, diskColor), direction)
      if connectingDiskPosition.isDefined
    yield connectingDiskPosition.get
  
  private def getDisksToFlip(diskPos: Position)(using disks: Map[Position, Disk]): Set[Position] =
    val diskColor: Color = disks(diskPos).color
    val disksPos: Set[Position] = disks.keySet
    for
      connectingDiskPos: Position <- getConnectingDisks(diskColor, diskPos)
      diskToFlip: Position <- disksPos
      if diskToFlip.inBetweenPos(diskPos, connectingDiskPos)
    yield diskToFlip

  /** Delegate method of [[BoardImpl.getAvailablePlacements()]].
   *  @param diskColor the color of the disk that needs to be placed.
   *  @return a [[scala.collection.immutable.Set]] of [[Position]] containing all the available placements positions.
   */
  def getAvailablePlacements(diskColor: Color): Set[Position] =
    @tailrec
    def _getNextEmptyPosition(diskPos: Position, direction: Position): Option[Position] =
      val neighbourPos = diskPos - direction
      neighbourPos match
        case p
          if (!p.inBounds(board.shape)) ||
            (board.disks.contains(p) && board.disks(p).color.equals(diskColor)) => Option.empty
        case p if board.disks.contains(p) => _getNextEmptyPosition(p, direction)
        case p => Some(p)

    given disks: Map[Position, Disk] = board.disks
    val capturableDisks = getCapturableNeighboursPair(diskColor)
    for
      (disk, neighbour) <- capturableDisks
      direction: Position = disk - neighbour
      availableMove: Option[Position] = _getNextEmptyPosition(neighbour, direction)
      if availableMove.isDefined
    yield availableMove.get

  /** Delegate method of [[BoardImpl.isPlacementValid()]].
   *  @param diskColor the color of the disk that wants to be placed.
   *  @param diskPos the position where the player wants to place the disk.
   *  @return `true` if the placement is valid, `false` otherwise.
   */
  def isPlacementValid(diskColor: Color, diskPos: Position): Boolean =
    getAvailablePlacements(diskColor).contains(diskPos)

  /** Delegate method of [[BoardImpl.placeDisk()]].
   *  @param diskColor the color of the disk that wants to be placed.
   *  @param diskPos the position where the player wants to place the disk. 
   *  @param validatePosition if `diskPos` needs to be validated or not.
   *  @return a new instance of [[Board]] with the disk placed and the disks captured.
   *  @throws java.lang.IllegalArgumentException if `diskPos` is not valid.
   */
  def placeDisk(diskColor: Color, diskPos: Position, validatePosition: Boolean): Board =
    def _captureDisks(using disks: Map[Position, Disk]): Map[Position, Disk] =
      val disksToFlip: Set[Position] = getDisksToFlip(diskPos)
      val disksAfterFlip = disksToFlip.map(pos => (pos, disks(pos).flip)).toMap
      disks ++ disksAfterFlip

    if validatePosition && !isPlacementValid(diskColor, diskPos)
      then throw IllegalArgumentException("The placement is not valid")
    given newDisks: Map[Position, Disk] = board.disks + (diskPos -> Disk(diskColor))
    Board(board.shape, _captureDisks)
