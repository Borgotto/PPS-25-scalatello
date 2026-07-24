package it.unibo.pps.model

import it.unibo.pps.domain.*
import it.unibo.pps.domain.Color.*
import it.unibo.pps.domain.MatchStatus.*
import it.unibo.pps.domain.OpponentType.*
import it.unibo.pps.model.board.Board
import it.unibo.pps.model.player.*
import it.unibo.pps.model.player.Opponent.*
import it.unibo.pps.state.{BoardState, MatchState}

/** Models the possible ways to interact with the logic of a match.
 *
 *  Implemented by: [[LogicImpl]]
 */
trait Logic:

  /** @return the current state of the match, represented by a [[MatchState]]
   *  instance.
   */
  def state: MatchState

  /** Handles the placement of a disk by the user, also determining the
   *  match status and the active player after the placement.
   *
   * @param position the position chosen by the user for the placement.
   * @return a new [[Logic]] instance that reflects the new state of the match.
   */
  def placeUserDisk(position: Position): Logic

  /** Handles the placement of a disk by the virtual opponent, also determining
   *  the match status and the active player after the placement.
   *
   *  @return a new [[Logic]] instance that reflects the new state of the match.
   */
  def placeOpponentDisk(): Logic

/** Implements the logic of a match.
 *
 *  Since every method that modifies the current state of the match returns a new
 *  [[Logic]], each [[LogicImpl]] instance is a snapshot of the current turn
 *  of the match.
 *
 *  A [[LogicImpl]] should be created only through the factory methods provided by the [[Logic]]
 *  companion object.
 *
 *  @param user the information about the user in this match (i.e. their color).
 *  @param opponent the information about the opponent in this match (i.e. their color
 *                 and placement strategy).
 *  @param status the current status of the match.
 *  @param activePlayer the player that must perform a placement in the current turn.
 *  @param board the board of the match.
 */
class LogicImpl(
  private val user: User,
  private val opponent: Opponent,
  private val status: MatchStatus,
  private val activePlayer: ActivePlayer,
  private val board: Board
) extends Logic:

  private val boardState: BoardState = activePlayer match
    case ActivePlayer.User => board.state.copy(
      userAvailablePlacements = board.getAvailablePlacements(user.color)
    )
    case ActivePlayer.Opponent => board.state

  def state: MatchState = MatchState(status, user, opponent, activePlayer, boardState)

  def placeUserDisk(position: Position): Logic = activePlayer match
    case ActivePlayer.Opponent => throw IllegalStateException("It is opponent's turn now")
    case ActivePlayer.User =>
      if !board.isPlacementValid(user.color, position) then this
      else getUpdatedLogic(user.color, position)

  def placeOpponentDisk(): Logic = activePlayer match
    case ActivePlayer.User => throw IllegalStateException("It is user's turn now")
    case ActivePlayer.Opponent =>
      val position = opponent.strategy.computePlacement(using board)
      getUpdatedLogic(opponent.color, position)

  private def areBothPlayersStuck(board: Board): Boolean =
    board.getAvailablePlacements(user.color).isEmpty
    && board.getAvailablePlacements(opponent.color).isEmpty

  private def getUpdatedStatus(board: Board): MatchStatus =
    if !areBothPlayersStuck(board) then InProgress
    else
      val numUserDisks = board.state.disks.count(_.color == user.color)
      val numOpponentDisks = board.state.disks.count(_.color == opponent.color)
      (numUserDisks, numOpponentDisks) match
        case (a, b) if a > b => UserWon
        case (a, b) if a < b => OpponentWon
        case _ => Tie

  private def otherPlayerColor: Color = activePlayer match
    case ActivePlayer.User => opponent.color
    case ActivePlayer.Opponent => user.color
  
  private def getUpdatedActivePlayer(board: Board): ActivePlayer =
    val availablePlacements = board.getAvailablePlacements(otherPlayerColor)
    if availablePlacements.isEmpty then activePlayer else activePlayer.next

  private def getUpdatedLogic(newDiskColor: Color, newDiskPosition: Position): Logic =
    val updatedBoard = board.placeDisk(newDiskColor, newDiskPosition)
    val updatedStatus = getUpdatedStatus(updatedBoard)
    val updatedActivePlayer = getUpdatedActivePlayer(updatedBoard)
    new LogicImpl(user, opponent, updatedStatus, updatedActivePlayer, updatedBoard)

/** Factory for [[Logic]] instances. */
object Logic:
  
  private def getUser(color: Color): User = User(color)
  
  private def getOpponent(color: Color, opponentType: OpponentType): Opponent =
    opponentType match
      case Random => RandomOpponent(color)
      case Easy => EasyOpponent(color)
      case Medium => MediumOpponent(color)
      case Hard => HardOpponent(color)

  private def getInitialActivePlayer(userColor: Color): ActivePlayer = userColor match
    case Black => ActivePlayer.User
    case White => ActivePlayer.Opponent

  /** Creates the logic for a new match that must be started from scratch.
   *
   * @param boardShape the shape that the board must have.
   * @param userColor the color that must be assigned to the user.
   * @param opponentType the type of the opponent, which determines their placement strategy.
   */
  def apply(boardShape: Shape, userColor: Color, opponentType: OpponentType): Logic =
    new LogicImpl(
      getUser(userColor),
      getOpponent(userColor.opposite, opponentType),
      InProgress,
      getInitialActivePlayer(userColor),
      Board(boardShape)
    )

  /** Creates the logic for a match that must be started from a specific match state.
   * 
   * This is meant to be used to resume a saved match.
   * 
   * @param state the state of the saved match to resume.
   */
  def apply(state: MatchState): Logic =
    val board = Board(state.board)
    new LogicImpl(state.user, state.opponent, state.status, state.activePlayer, board)

  /** Creates the logic for a new match that must be started from scratch, 
   *  but providing a specific [[Board]] instance.
   *
   *  This is meant to be used only for testing purposes, in order to provide a
   *  specific board configuration to ease unit testing.
   *
   * @param userColor the color that must be assigned to the user.
   * @param opponentType the type of the opponent, which determines their placement strategy.
   * @param board the provided [[Board]] instance.
   */
  private[model] def apply(userColor: Color, opponentType: OpponentType, board: Board): Logic =
    new LogicImpl(
      getUser(userColor),
      getOpponent(userColor.opposite, opponentType),
      InProgress,
      getInitialActivePlayer(userColor),
      board
    )
