package it.unibo.pps.model.board

import it.unibo.pps.model.board.BoardCreationExtensions.toPosDiskMap
import it.unibo.pps.utils.IntExtensions.half
import it.unibo.pps.model.board.posComputations.{PosComputeExtensions, PosComputeExtensionsRectangle}
import it.unibo.pps.state.{BoardState, DiskState}
import it.unibo.pps.utils.{Color, Position, Shape}

/** Defines the board where a [[User]] and an [[Opponent]] can place disks to play the game.
 *
 * This provides methods to:
 *    - get a new board instance with the [[disks]] updated after placing a disk
 *      and capturing the disks accordingly: [[placeDisk()]];
 *    - know if a placement is valid: [[isPlacementValid()]];
 *    - know all available placements for a specified disk: [[getAvailablePlacements()]];
 *    - get its [[state]].
 *
 * All of them must be implemented in classes using this.
 *
 * Used by: [[BoardImpl]]
 */
trait Board:
  /** @return the [[Shape]] of this board */
  private[board] def shape: Shape

  /**
   * @return the [[scala.collection.immutable.Map]] of the disks on this board,
   *         with [[Position]] as `keys` and [[Disk]] as `values`.
   */
  private[board] def disks: Map[Position, Disk]

  /** @return the [[BoardState]] of this board. */
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

  /** Places a new disk and captures the disks on the board accordingly.
   *
   * Capturing a disk means that the disk will be flipped to change to the color of the one capturing it.
   *
   * A disk is captured if it is of the other color than the placed one and
   * is enclosed between the placed disk and another disk of the same color of the placed disk.
   *
   * @param diskColor the color of the disk that wants to be placed.
   * @param diskPos the position where the player wants to place the disk.
   * @return a new instance of board with the disk placed.
   */
  def placeDisk(diskColor: Color, diskPos: Position, validatePosition: Boolean = true): Board

/** Factory for [[Board]] instances.
 * 
 * Provides a factory to create a board starting from:
 * - a [[Shape]]: creates the initial board;
 * - a [[BoardState]]: creates a board with that state;
 * - a [[Shape]] and a [[scala.collection.immutable.Map]] of [[Position]] -> [[Disk]]: creates a board with that attributes.
 */
object Board:
  /** Given a shape, creates a board with the initial disks configuration.
   *
   * @param shape the [[Shape]] of the board that will be created.
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

  /** Given a state, creates a board with that configuration.
   * @param state the [[BoardState]] from where the board will be created.
   */
  def apply(state: BoardState): Board =
    val disks = state.disks.map(disk => disk.position -> Disk(disk.color)).toMap
    apply(state.shape, disks)

  /** Given a shape and some disks, creates a board.
   * @param shape the [[Shape]] of the board that will be created.
   * @param disks the disks that will be on the board.
   */
  def apply(shape: Shape, disks: Map[Position, Disk]): Board =
    shape match
      case _ =>
        given contextComputations: PosComputeExtensions = PosComputeExtensionsRectangle()
        BoardImpl(shape, disks)

/** Implements a generic board.
 * This uses a `given` of type: [[PosComputeExtensions]] to use the correct computations on different boards.
 *
 * Delegates the computations of its methods to an instance of the class [[BoardComputations]].
 *
 * Extends the trait: [[Board]].
 * @param shape implements [[Board.shape]], the shape of this board.
 *
 * @param disks implements [[Board.disks]], it represents the disks on this board.
 *
 * @param posComputations the context of [[PosComputeExtensions]],
 *                        it contains the different computations that may need to change for different types of boards.
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
   * It is the state of this [[Board]].
   */
  lazy val state: BoardState = BoardState(shape, disks.map((pos, disk) => DiskState(disk.color, pos)).toSet, Set())

  /** Implements [[Board.getAvailablePlacements()]].
   * @param diskColor the [[Color]] of the disk that needs to be placed.
   * @return a [[scala.collection.immutable.Set]] of [[Position]] containing the position of all the available placements.
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
   * @return a new instance of board with the disk placed.
   */
  def placeDisk(diskColor: Color, diskPos: Position, validatePosition: Boolean = true): Board =
    compute.placeDisk(diskColor, diskPos, validatePosition)

  /** @inheritdoc
   * @param obj what will be confronted with this board.
   * @return `true` if this board is equal to another board, `false` otherwise.
   */
  override def equals(obj: Any): Boolean =
    obj match
      case b: Board => disks.equals(b.disks) && shape.equals(b.shape)
