package it.unibo.pps.model.board

import it.unibo.pps.model.board.posComputations.PosComputeExtensions
import it.unibo.pps.utils.{Color, Position}

import scala.annotation.tailrec

/** The delegate class of [[BoardImpl]].
 *
 * This helper class computes all the methods of its delegator.
 * @param board the [[Board]] context, to make this class operate on the correct instance of board.
 * @param posComputations the [[PosComputeExtensions]] context,
 *                        to make this class use the appropriate methods on different Boards.
 */
private[board] class BoardComputations(using board: Board)(using posComputations: PosComputeExtensions):
  /** Helper method to get all the neighbour disks with the opposite color of a disk.
   * @param diskColor the color of a disk
   * @return a [[scala.collection.immutable.Set]] of a [[scala.Tuple2]] of ([[Position]], [[Position]]),
   *         the first position is the position of a disk that has the color equal to `diskColor`,
   *         the second position is the position of one of its disks neighbours with the opposite color,
   *         there is a tuple for each neighbour.
   */
  private def getOppositeColorNeighbours(diskColor: Color)
                                        (using disks: Map[Position, Disk]): Set[(Position, Position)] =
    for diskPos: Position <- disks.filter((_, disk) => disk.color.equals(diskColor)).keySet
        possibleNeighbourPos: Position <- disks.filter((_, disk) => disk.color.equals(diskColor.opposite)).keySet
        if possibleNeighbourPos.inNeighbourhood(diskPos)
    yield (diskPos, possibleNeighbourPos)

  /** Helper method to get all the disks that connects to the placed disk.
   *
   * Two disks connects if they are the same color and between them there are only disks (at least one)
   *      of the opposite color.
   * @param diskColor the color of the placed disk.
   * @param placedDiskPos the position where the disk will be placed.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] containing
   *         the position of all the disks that connects to the placed disk.
   */
  private def getConnectingDisks(diskColor: Color, placedDiskPos: Position)
                                (using disks: Map[Position, Disk]): Set[Position] =
    /** Helper method to find the disks that connect to the placed disk.
     * @param diskPos the position of the considered disk.
     * @param placedDisk [[scala.Tuple2]] of ([[Position]], [[Color]]) of the placed disk.
     * @param direction the direction in which to look for a possible connecting disk.
     * @return a [[scala.Option]] of the connecting disk [[Position]] if found, [[scala.Option.empty]] otherwise.
     */
    @tailrec
    def _getNextConnectingDisk(diskPos: Position, placedDisk: (Position, Color), direction: Position): Option[Position] =
      val neighbourPos = diskPos - direction
      val disksWithoutPlacedDisk: Map[Position, Disk] = disks.filter(e => !e.equals(placedDisk))
      neighbourPos match
        case p if !p.inBounds(board.shape) || !disks.contains(p) => Option.empty
        case p if disksWithoutPlacedDisk(p).color.equals(placedDisk._2.opposite) =>
          _getNextConnectingDisk(p, placedDisk, direction)
        case p => Some(p)

    val oppositeNeighboursPos: Set[(Position, Position)] =
      getOppositeColorNeighbours(diskColor).filter((disk, neighbour) => disk.equals(placedDiskPos))
    for (disk, neighbour) <- oppositeNeighboursPos
        diskPosition: Position = Position(neighbour.row, neighbour.column)
        direction: Position = disk - neighbour
        connectingDiskPosition: Option[Position] =
          _getNextConnectingDisk(diskPosition, (placedDiskPos, diskColor), direction)
        if connectingDiskPosition.isDefined
    yield connectingDiskPosition.get

  /** Helper method to find which disks needs to be flipped.
   * @param diskPos the position of the placed disk.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] containing the position of all the disks to flip.
   */
  private def getDisksToFlip(diskPos: Position)(using disks: Map[Position, Disk]): Set[Position] =
    val diskColor: Color = disks(diskPos).color
    for connectingDiskPos: Position <- getConnectingDisks(diskColor, diskPos)
        diskToFlip: Position <- disks.keySet
        if diskToFlip.inBetweenPos(diskPos, connectingDiskPos)
    yield diskToFlip

  /** Delegate method of [[BoardImpl.getAvailablePlacements()]].
   * @param diskColor the color of the disk that needs to be placed.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] containing all the available placements positions.
   */
  def getAvailablePlacements(diskColor: Color): Set[Position] =
    /** Helper method to find the empty position next to a neighbour of the opposite color.
     * @param diskPos the position of the considered disk.
     * @param direction the direction in which to look for a possible empty position.
     * @return a [[scala.Option]] of the empty [[Position]] if found, [[scala.Option.empty]] otherwise.
     */
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
    for (disk, neighbour) <- getOppositeColorNeighbours(diskColor)
        diskPos: Position = Position(neighbour.row, neighbour.column)
        direction: Position = disk - neighbour
        availableMove: Option[Position] = _getNextEmptyPosition(diskPos, direction)
        if availableMove.isDefined
    yield availableMove.get

  /** Delegate method of [[BoardImpl.isPlacementValid()]].
   * @param diskColor the color of the disk that wants to be placed.
   * @param diskPos the position where the player wants to place the disk.
   * @return `true` if the placement is valid, `false` otherwise.
   */
  def isPlacementValid(diskColor: Color, diskPos: Position): Boolean =
    getAvailablePlacements(diskColor).contains(diskPos)

  /** Delegate method of [[BoardImpl.placeDisk()]].
   * @param diskColor the color of the disk that wants to be placed.
   * @param diskPos the position where the player wants to place the disk.
   * @return a new instance of [[Board]] with the disk placed and the disks captured.
   * @throws IllegalArgumentException if `diskPos` is not valid
   */
  def placeDisk(diskColor: Color, diskPos: Position): Board =
    /** Helper method to capture the correct disks after placing a new disk.
     * @param disks the disks on the board after placing a new disk.
     * @return the new disks on the board after capturing the correct ones.
     */
    def captureDisks(using disks: Map[Position, Disk]): Map[Position, Disk] =
      val disksToFlip: Set[Position] = getDisksToFlip(diskPos)
      val disksAfterFlip = disks.map((pos, disk) => (pos, if disksToFlip.contains(pos) then disk.flip else disk))
      disksAfterFlip

    if !isPlacementValid(diskColor, diskPos) then throw IllegalArgumentException("The placement is not valid")
    given newDisks: Map[Position, Disk] = board.disks + (diskPos -> Disk(diskColor))
    Board(board.shape, captureDisks)
