package it.unibo.pps.controller

import it.unibo.pps.controller.save.SaveManager.SaveManagers.MatchStateSaveManager
import it.unibo.pps.model.Logic
import it.unibo.pps.observer.{Publisher, Subscriber}
import it.unibo.pps.state.MatchState
import it.unibo.pps.state.PlayerState
import it.unibo.pps.utils.MatchStatus
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.View

import scala.annotation.tailrec
import scala.util.{Success, Try}
import os.Path

/** Defines the main controller of the application.
 *
 * This provides methods to:
 * - start the match: [[startMatch()]];
 * - handle the turns of the user and the opponent: [[handleSelection()]];
 * - save the match: [[saveMatch()]];
 * - load the match: [[loadMatch()]];
 * - get the file names of the saves: [[saveFileNames]];
 * - delete a save file: [[deleteSaveFile()]].
 *
 * All of them must be implemented in classes using this.
 *
 * Used by: [[MatchControllerImpl]].
 */
trait MatchController:
  /** Starts the match using the settings selected by the user.
   *
   * @param boardShape the [[Shape]] of the [[Board]] selected by the user.
   * @param userColor  the [[Color]] selected by the user.
   */
  def startMatch(boardShape: Shape, userColor: Color): Unit

  /** Given the selected position, handles the user turn and then the opponent turn(s) afterwords.
   * @param position the [[Position]] selected by the user.
   */
  def handleSelection(position: Position): Unit

  /** Saves the current state of the match.
   * @param fileName the name of the file in which the state of the match will be saved.
   * @return [[scala.util.Success]] if the match is saved correctly, [[scala.util.Failure]] otherwise.
   */
  def saveMatch(fileName: String): Try[_]

  /** Loads a match from the selected file.
   * @param fileName the name of the file from which the user wants to load the match.
   * @return [[scala.util.Success]] if the match is loaded correctly, [[scala.util.Failure]] otherwise.
   */
  def loadMatch(fileName: String): Try[_]

  /**
   * @return the names of the files where is stored a save.
   */
  def saveFileNames: Seq[String]

  /** Deletes a save file.
   * @param fileName the name of the file that the user wants to delete.
   * @return [[scala.util.Success]] if the file is deleted correctly, [[scala.util.Failure]] otherwise.
   */
  def deleteSaveFile(fileName: String): Try[_]

/** Factory for [[MatchController]] instances
 *
 * Provides a factory to create a controller starting from:
 * - a [[View]]: creates a controller and adds the view to the list of subscribers,
 *    to be able to update it with the state of the match.
 */
object MatchController:
  /** Given a [[View]] instantiates a controller and then adds the `view` to the list of subscribers to update it with
   * the state of the match every time something changes.
   * @param view the view of the application.
   */
  def apply(view: View): MatchController =
    val controller = MatchControllerImpl()
    controller.subscribe(view)
    controller

/** Implements the controller of the application.
 *
 * This controller is the [[Publisher]] that updates all its subscribers about the [[MatchState]].
 *
 * Extends the trait: [[MatchController]] and [[Publisher]].
 */
class MatchControllerImpl extends MatchController, Publisher[MatchState]:
  /** The logic of the application. */
  private var logic: Logic = _
  /** The sequence of the subscribers to update with the [[MatchState]], */
  private var subscribers = Seq[Subscriber[MatchState]]()

  /** the directory where are stored the save files, */
  private val saveDirectory = os.home / ".scalatello"
  /** the [[MatchStateSaveManager]] of the application. */
  private val saveManager = MatchStateSaveManager(saveDirectory)

  /** @inheritdoc
   * Implements [[Publisher.subscribe()]]
   * @param subscriber a new subscriber that wants to subscribe to get the updates.
   */
  override def subscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers :+ subscriber

  /** @inheritdoc
   * Implements [[Publisher.unsubscribe()]]
   * @param subscriber a subscriber that no longer wants to get the updates.
   */
  override def unsubscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers.filter(s => s != subscriber)

  /** @inheritdoc
   * Implements [[Publisher.notifySubscribers()]].
   * @param state the [[MatchState]] to notify the subscribers with.
   */
  override def notifySubscribers(state: MatchState): Unit =
    subscribers.foreach(s => s.update(state))

  /** @return `true` if the match is over, `false` otherwise. */
  private def isMatchOver: Boolean = logic.state.status != MatchStatus.InProgress

  /** Handles the opponent turn and then updates the subscribers. */
  @tailrec
  private def handleOpponentTurn(): Unit =
    logic.state.activePlayer match
      case PlayerState.Opponent(_, _) =>
        logic = logic.placeOpponentDisk()
        notifySubscribers(logic.state)
        if !isMatchOver then handleOpponentTurn()
      case _ => ()

  /** @inheritdoc
   * And notifies the subscribers.
   *
   * Implements [[MatchController.startMatch()]].
   * @param boardShape the [[Shape]] of the [[Board]] selected by the user.
   * @param userColor  the [[Color]] selected by the user.
   */
  def startMatch(boardShape: Shape, userColor: Color): Unit =
    logic = Logic(boardShape, userColor)
    notifySubscribers(logic.state)
    logic.state.activePlayer match
      case PlayerState.Opponent(_, _) => handleOpponentTurn()
      case _ => ()

  /** @inheritdoc
   * And notifies the subscribers.
   *
   * Implements [[MatchController.handleSelection()]].
   * @param position the [[Position]] selected by the user.
   */
  def handleSelection(position: Position): Unit =
    logic = logic.placeUserDisk(position)
    notifySubscribers(logic.state)
    if !isMatchOver then handleOpponentTurn()

  /** @inheritdoc
   * Implements [[MatchController.saveMatch()]].
   * @param fileName the name of the file in which the state of the match will be saved.
   * @return [[scala.util.Success]] if the match is saved correctly, [[scala.util.Failure]] otherwise.
   */
  def saveMatch(fileName: String): Try[_] =
    given filepath: Path = saveDirectory / fileName
    saveManager.save(logic.state)

  /** @inheritdoc
   * And if it is a success notifies the subscribers.
   *
   * Implements [[MatchController.loadMatch()]].
   * @param fileName the name of the file from which the user wants to load the match.
   * @return [[scala.util.Success]] if the match is loaded correctly, [[scala.util.Failure]] otherwise.
   */
  def loadMatch(fileName: String): Try[_] =
    given filepath: Path = saveDirectory / fileName
    val result = saveManager.load 
    result match
      case Success(matchState: MatchState) => logic = Logic(matchState)
      case _ => ()
    result

  /** Implements [[MatchController.saveFileNames]].
   *  @return the names of the files where is stored a save.
   */
  def saveFileNames: Seq[String] = saveManager.saveFileNames

  /** @inheritdoc
   * Implements [[MatchController.deleteSaveFile()]].
   * @param fileName the name of the file that the user wants to delete.
   * @return [[scala.util.Success]] if the file is deleted correctly, [[scala.util.Failure]] otherwise.
   */
  def deleteSaveFile(fileName: String): Try[_] =
    given filepath: Path = saveDirectory / fileName
    saveManager.deleteSaveFile
