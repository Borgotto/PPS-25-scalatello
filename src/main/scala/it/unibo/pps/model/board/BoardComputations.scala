package it.unibo.pps.model.board

import it.unibo.pps.utils.{Color, Position}

import scala.annotation.tailrec

/** The delegate class of [[BoardImpl]].
 *
 * This helper class computes all the methods of [[BoardImpl]].
 * @param board the [[Board]] context, to make this class operate on the correct instance of [[BoardImpl]].
 * @param posComputations the [[PosComputeExtensions]] context,
 *                        to make this class use the appropriate methods on different Boards.
 */
private[board] class BoardComputations(using board: Board)(using posComputations: PosComputeExtensions):
  /** Helper method to get all the neighbour disks with the opposite color of a disk.
   * @param diskColor the color of a disk
   * @return a [[scala.collection.immutable.Set]] of a [[scala.Tuple2]] of ([[Position]], [[Position]]),
   *         the first [[Position]] is the position of a disk that has the color equal to `diskColor`,
   *         the second [[Position]] is the position of one of its disks neighbours with the opposite color,
   *         there is a tuple for each neighbour.
   */
  private def getOppositeColorNeighbours(diskColor: Color): Set[(Position, Position)] =
    for diskPos: Position <- board.disks.filter((_, disk) => disk.color.equals(diskColor)).keySet
        possibleNeighbourPos: Position <- board.disks.filter((_, disk) => disk.color.equals(diskColor.opposite)).keySet
        if possibleNeighbourPos.inNeighbourhood(diskPos)
    yield (diskPos, possibleNeighbourPos)

  /** Helper method to get all the disks that connects to the placed disk.
   *
   * Two disks connects if they are the same color and between them there are only disks (at least one)
   *      of the opposite color.
   * @param oppositeNeighboursPos the result of [[BoardComputations.getOppositeColorNeighbours(diskColor)]].
   * @param diskColor the color of the placed disk.
   * @param placedDiskPos the position where the disk will be placed.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] with inside
   *         the position of all the disks that connects to the placed disk.
   */
  private def getConnectingDisks(oppositeNeighboursPos: Set[(Position, Position)], 
                                 diskColor: Color, placedDiskPos: Position): Set[Position] =
    /** Helper method to find the disks that connect to the placed disk.
     * @param diskPos the position of the considered disk.
     * @param placedDisk [[scala.Tuple2]] of ([[Position]], [[Color]]) of the placed disk.
     * @param direction the direction in which to look for a possible connecting disk.
     * @return a [[scala.Option]] of the connecting disk [[Position]] if found, [[scala.Option.empty]] otherwise.
     */
    @tailrec
    def _getNextConnectingDisk(diskPos: Position, placedDisk: (Position, Color), direction: Position): Option[Position] =
      val neighbourPos = diskPos - direction
      val disksWithoutPlacedDisk: Map[Position, Disk] = board.disks.filter(e => !e.equals(placedDisk))
      neighbourPos match
        case p if !p.inBounds(board.shape) || !board.disks.contains(p) => Option.empty
        case p if disksWithoutPlacedDisk(p).color.equals(placedDisk._2.opposite) =>
          _getNextConnectingDisk(p, placedDisk, direction)
        case p => Some(p)

    for (disk, neighbour) <- oppositeNeighboursPos
        diskPosition: Position = Position(neighbour.row, neighbour.column)
        direction: Position = disk - neighbour
        connectingDiskPosition: Option[Position] =
          _getNextConnectingDisk(diskPosition, (placedDiskPos, diskColor), direction)
        if connectingDiskPosition.isDefined
    yield connectingDiskPosition.get

  /** Helper method to find which disks needs to be flipped.
   * @param diskPos the position of the placed disk.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] with inside the position of all the disks to flip.
   */
  private def getDisksToFlip(diskPos: Position): Set[Position] =
    val diskColor: Color = board.disks(diskPos).color
    val oppositeNeighboursPos: Set[(Position, Position)] =
      getOppositeColorNeighbours(diskColor).filter((disk, neighbour) => disk.equals(diskPos))
    for connectingDiskPos: Position <- getConnectingDisks(oppositeNeighboursPos, diskColor, diskPos)
        diskToFlip: Position <- board.disks.keySet
        if diskToFlip.inBetween(diskPos, connectingDiskPos)
    yield diskToFlip

  /** Delegate method of [[BoardImpl.getAvailablePlacements()]].
   * @param diskColor the color of the disk that needs to be placed.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] with inside all the available placements positions.
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
   * @return a new instance of [[Board]] with the disk placed.
   * @throws IllegalArgumentException if `diskPos` is not valid
   */
  def placeDisk(diskColor: Color, diskPos: Position): Board =
    if !isPlacementValid(diskColor, diskPos) then throw IllegalArgumentException("The placement is not valid")
    Board(board.shape, board.disks + (diskPos -> Disk(diskColor)))

  /** Delegate method of [[BoardImpl.captureDisks()]].
   * @param diskPos the position of the placed disk.
   * @return a new instance of [[Board]] with the captured disks flipped.
   * @throws IllegalArgumentException if `diskPos` is not on the board.
   */
  def captureDisks(diskPos: Position): Board =
    if !board.disks.contains(diskPos) then throw IllegalArgumentException("There isn't a disk in that position")
    val disksToFlip: Set[Position] = getDisksToFlip(diskPos)
    val disksAfterFlip = board.disks.map((pos, disk) => (pos, if disksToFlip.contains(pos) then disk.flip else disk))
    Board(board.shape, disksAfterFlip)
