package it.unibo.pps.model.board

import it.unibo.pps.model.board.BoardCreationExtensions.{toPosDiskMap, half}
import it.unibo.pps.state.{BoardState, DiskState}
import it.unibo.pps.utils.{Color, Position, Shape}

/** Defines the board where the player can place disks to play the game.
 *
 * This provides methods to:
 *    - get a new [[Board]] instance with the [[disks]] updated;
 *    - know if a placement is valid;
 *    - know all available placements for a specified disk and;
 *    - get its [[state]].
 *
 * All of them must be specified in classes using this.
 *
 * Used by: [[BoardImpl]]
 */
trait Board:
  /** @return the [[Shape]] of this [[Board]] */
  private[board] def shape: Shape

  /**
   * @return the [[scala.collection.immutable.Map]] of the disks on this [[Board]],
   *         with [[Position]] as `keys` and [[Disk]] as `values`.
   */
  private[board] def disks: Map[Position, Disk]

  /** @return the [[BoardState]] of this [[Board]]. */
  def state: BoardState

  /**
   * @param diskColor the color of the disk that needs to be placed.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] containing all the available placements positions.
   */
  def getAvailablePlacements(diskColor: Color): Set[Position]

  /** Validates a placement.
   * @param diskColor the color of the disk that wants to be placed.
   * @param diskPos the position where the player wants to place the disk.
   * @return `true` if the placement is valid, `false` otherwise.
   */
  def isPlacementValid(diskColor: Color, diskPos: Position): Boolean

  /** Places a new disk.
   * @param diskColor the color of the disk that wants to be placed.
   * @param diskPos the position where the player wants to place the disk.
   * @return a new instance of [[Board]] with the disk placed.
   */
  def placeDisk(diskColor: Color, diskPos: Position): Board

  /** Given the position of the last placed disk at the time,
   * captures the correct disks already present on the [[Board]].
   * @param diskPos the position of the placed disk.
   * @return a new instance of [[Board]] with the captured disks flipped.
   */
  def captureDisks(diskPos: Position): Board

/** Factory for [[it.unibo.pps.model.board.Board]] instances */
object Board:
  /** Given a shape, creates a [[Board]] with the initial disks configuration.
   *
   * Uses [[BoardCreationExtensions]] methods to easily create a [[Board]]
   * @param shape the shape of the [[Board]] that will be created.
   */
  def apply(shape: Shape): Board =
    val bottomRightCenterPos: Position =
      shape match
        case Shape.Square(n) => (n.half, n.half)
        case Shape.Rectangle(h, w) => (h.half, w.half)

    val initialDisks: Map[Position, Disk] =
      s"""
        ${bottomRightCenterPos.left.up} -> W
        ${bottomRightCenterPos.up} -> B
        ${bottomRightCenterPos.left} -> B
        $bottomRightCenterPos -> W
      """.toPosDiskMap
    
    apply(shape, initialDisks)

  /** Given a state, creates a [[Board]] with that configuration.
   * @param state the state from where the [[Board]] will be created.
   */
  def apply(state: BoardState): Board =
    val disks = state.disks.map(disk => disk.position -> Disk(disk.color)).toMap
    apply(state.shape, disks)

  /** Given a shape and some disks, creates a [[Board]].
   * @param shape the shape of the [[Board]] that will be created.
   * @param disks the disks that will be on the [[Board]].
   */
  def apply(shape: Shape, disks: Map[Position, Disk]): Board =
    shape match
      case _ =>
        given contextComputations: PosComputeExtensions = PosComputeExtensionsRectangle()
        BoardImpl(shape, disks)

/** Implements a generic [[Board]].
 * This uses a [[given]] of type: [[PosComputeExtensions]] to use the correct computations on the different Boards.
 *
 * Delegates the computations of its methods to an instance of the class [[BoardComputations]].
 *
 * Extends the trait: [[Board]].
 * @param shape implements [[Board.shape]], the shape of this [[Board]].
 *
 * @param disks implements [[Board.disks]], it represents the disks on this [[Board]].
 *
 * @param posComputations the context of [[PosComputeExtensions]],
 *                        it contains the different computations that may need to change for different types of Boards.
 */
private[board] class BoardImpl(val shape: Shape, val disks: Map[Position, Disk])
                           (using posComputations: PosComputeExtensions) extends Board:
  /** Used by: [[compute]].
   * @return the `given` of this [[Board]].
   */
  private given contextBoard: Board = this

  /** The delegate of [[BoardImpl]]. */
  private val compute: BoardComputations = BoardComputations()

  /** Implements [[Board.state]].
   *
   * It is the current state of this [[Board]].
   */
  val state: BoardState = BoardState(shape, disks.map((pos, disk) => DiskState(disk.color, pos)).toSet, Set())

  /** Implements [[Board.getAvailablePlacements()]].
   * @param diskColor the color of the disk that needs to be placed.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] containing all the available placements positions.
   */
  def getAvailablePlacements(diskColor: Color): Set[Position] =
    compute.getAvailablePlacements(diskColor)

  /** Implements [[Board.isPlacementValid()]].
   * @param diskColor the color of the disk that wants to be placed.
   * @param diskPos the position where the player wants to place the disk.
   * @return `true` if the placement is valid, `false` otherwise.
   */
  def isPlacementValid(diskColor: Color, diskPos: Position): Boolean =
    compute.isPlacementValid(diskColor, diskPos)

  /** Implements [[Board.placeDisk()]].
   * @param diskColor the color of the disk that wants to be placed.
   * @param diskPos the position where the player wants to place the disk.
   * @return a new instance of [[Board]] with the disk placed.
   */
  def placeDisk(diskColor: Color, diskPos: Position): Board =
    compute.placeDisk(diskColor, diskPos)

  /** Implements [[Board.captureDisks()]].
   * @param diskPos the position of the placed disk.
   * @return a new instance of [[Board]] with the captured disks flipped.
   */
  def captureDisks(diskPos: Position): Board =
    compute.captureDisks(diskPos)

  /** @inheritdoc
   * @param obj what will be confronted with this [[Board]].
   * @return `true` if this [[Board]] is equal to another [[Board]], `false` otherwise.
   */
  override def equals(obj: Any): Boolean =
    obj match
      case b: Board => state.disks.equals(b.state.disks) && shape.equals(b.state.shape)
