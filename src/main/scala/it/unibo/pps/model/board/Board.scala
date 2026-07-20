package it.unibo.pps.model.board

import it.unibo.pps.utils.IntExtensions.half
import it.unibo.pps.model.board.BoardCreationExtensions.toPosDiskMap
import it.unibo.pps.domain.{Color, Position, Shape}
import it.unibo.pps.model.board.computations.{BoardComputations, ComputationsExtensions, ComputationsExtensionsRectangle}
import it.unibo.pps.state.{BoardState, DiskState}

/** Defines the board where a [[User]] and an [[Opponent]] can place disks to play the game.
 *
 *  All methods must be specified by every class using it.
 *
 *  Used by: [[BoardImpl]]
 */
trait Board:
  /** @return the [[Shape]] of this board */
  private[board] def shape: Shape

  /** @return the [[scala.collection.immutable.Map]] of the disks on this board,
   *         with [[Position]] as `keys` and [[Disk]] as `values`.
   */
  private[board] def disks: Map[Position, Disk]

  /** @return the [[BoardState]] of this board. */
  def state: BoardState

  /**
   *  @param diskColor the color of the disk that needs to be placed.
   *  @return a [[scala.collection.immutable.Set]] of [[Position]] containing all the available placements positions.
   */
  def getAvailablePlacements(diskColor: Color): Set[Position]

  /** Validates a placement.
   *  @param diskColor the color of the disk that wants to be placed.
   *  @param diskPos the position where the player wants to place the disk.
   *  @return `true` if the placement is valid, `false` otherwise.
   */
  def isPlacementValid(diskColor: Color, diskPos: Position): Boolean

  /** Places a new disk and captures the disks on the board accordingly.
   *
   *  Capturing a disk means that the disk will be flipped to change to the color of the one capturing it.
   *
   *  A disk is captured if it is of the other color than the placed one and is enclosed between the placed disk 
   *  and another disk of the same color of the placed disk.
   *  @param diskColor the color of the disk that wants to be placed.
   *  @param diskPos the position where the player wants to place the disk. 
   *  @param validatePosition if `diskPos` needs to be validated or not, default = `true`.
   *  @return a new instance of board with the disk placed and the disks captured.
   */
  def placeDisk(diskColor: Color, diskPos: Position, validatePosition: Boolean = true): Board

/** Factory for [[Board]] instances. */
object Board:
  /** Given a shape, creates a board with the initial disks configuration.
   *  @param shape the [[Shape]] of the board that will be created.
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
   *  @param state the [[BoardState]] from where the board will be created.
   */
  def apply(state: BoardState): Board =
    val disks = state.disks.map(disk => disk.position -> Disk(disk.color)).toMap
    apply(state.shape, disks)

  /** Given a shape and some disks, creates a board.
   *  @param shape the [[Shape]] of the board that will be created.
   *  @param disks the disks that will be on the board.
   */
  def apply(shape: Shape, disks: Map[Position, Disk]): Board =
    shape match
      case _ =>
        given contextComputations: ComputationsExtensions = ComputationsExtensionsRectangle()
        BoardImpl(shape, disks)

/** Implements a generic board.
 * 
 *  Delegates the computations of its methods to an instance of the class [[BoardComputations]].
 *  @param shape the shape of this board.
 *  @param disks the disks on this board.
 *  @param posComputations the context of [[ComputationsExtensions]], it contains the different computations 
 *                         that may need to change for different types of boards.
 */
private[board] class BoardImpl(val shape: Shape, val disks: Map[Position, Disk])
                           (using posComputations: ComputationsExtensions) extends Board:
  private given contextBoard: Board = this
  private val compute: BoardComputations = BoardComputations()

  /** The [[BoardState]] of this board. */
  lazy val state: BoardState = BoardState(shape, disks.map((pos, disk) => DiskState(disk.color, pos)).toSet, Set())

  def getAvailablePlacements(diskColor: Color): Set[Position] =
    compute.getAvailablePlacements(diskColor)

  def isPlacementValid(diskColor: Color, diskPos: Position): Boolean =
    compute.isPlacementValid(diskColor, diskPos)

  def placeDisk(diskColor: Color, diskPos: Position, validatePosition: Boolean = true): Board =
    compute.placeDisk(diskColor, diskPos, validatePosition)

  override def equals(obj: Any): Boolean =
    obj match
      case b: Board => disks.equals(b.disks) && shape.equals(b.shape)
