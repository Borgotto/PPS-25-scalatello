package it.unibo.pps.controller

import it.unibo.pps.controller.save.SaveManager.SaveManagers.MatchStateSaveManager
import it.unibo.pps.domain.{Color, OpponentType, Position, Shape}
import it.unibo.pps.model.Logic
import it.unibo.pps.domain.ActivePlayer.{Opponent, User}
import it.unibo.pps.observer.{Publisher, Subscriber}
import it.unibo.pps.state.MatchState
import it.unibo.pps.domain.MatchStatus.InProgress
import it.unibo.pps.view.View

import scala.annotation.tailrec
import scala.util.{Success, Try}
import os.Path

/** 
 * Defines the main controller of the application.
 */
trait Controller extends Publisher[MatchState]:
  
  /** 
   * Starts and configures a match according to the passed settings.
   * @param boardShape the [[Shape]] the [[Board]] must have.
   * @param userColor  the [[Color]] assigned to the user.
   */
  def startMatch(boardShape: Shape, userColor: Color): Unit

  /** 
   * Handles the current turn of the user, according to the position selected by them for their next placement.
   * @param position the position selected by the user.
   */
  def handleSelection(position: Position): Unit

  /** 
   * Saves the current state of the match.
   * @param fileName the name of the save file to create.
   * @return [[scala.util.Success]] if the save is successful, [[scala.util.Failure]] otherwise.
   */
  def saveMatch(fileName: String): Try[_]

  /** 
   * Loads a match from a save file.
   * @param fileName the name of the save file from which the match must be loaded.
   * @return [[scala.util.Success]] if the match is loaded correctly, [[scala.util.Failure]] otherwise.
   */
  def loadMatch(fileName: String): Try[_]

  /**
   * @return the names of the save files.
   */
  def saveFileNames: Seq[String]

  /** 
   * Deletes a save file.
   * @param fileName the name of the save file to delete.
   * @return [[scala.util.Success]] if the file is deleted successfully, [[scala.util.Failure]] otherwise.
   */
  def deleteSaveFile(fileName: String): Try[_]

/** 
 * Defines factories for [[Controller]] instances.
 */
object Controller:
  
  /** 
   * Instantiates a [[Controller]] and then subscribes the [[View]] to the updates
   * published by the [[Controller]].
   * @param view the view of the application.
   */
  def apply(view: View): Controller =
    val controller = MatchController()
    controller.subscribe(view)
    controller

/** 
 * Implements the controller of the application.
 */
class MatchController extends Controller:
  private var logic: Logic = _
  private var subscribers = Seq[Subscriber[MatchState]]()

  private val saveDirectory = os.home / ".scalatello"
  private val saveManager = MatchStateSaveManager(saveDirectory)
  
  def subscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers :+ subscriber
  
  def unsubscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers.filter(s => s != subscriber)
  
  def notifySubscribers(state: MatchState): Unit =
    subscribers.foreach(s => s.update(state))

  def startMatch(boardShape: Shape, userColor: Color): Unit =
    logic = Logic(boardShape, userColor, OpponentType.Random)
    notifySubscribers(logic.state)
    logic.state.activePlayer match
      case Opponent => handleOpponentTurn()
      case _ => ()

  private def isMatchOver: Boolean = logic.state.status != InProgress

  @tailrec
  private def handleOpponentTurn(): Unit =
    logic.state.activePlayer match
      case Opponent =>
        logic = logic.placeOpponentDisk()
        notifySubscribers(logic.state)
        if !isMatchOver then handleOpponentTurn()
      case _ => ()
  
  def handleSelection(position: Position): Unit =
    if !isMatchOver then
      logic = logic.placeUserDisk(position)
      notifySubscribers(logic.state)
      if !isMatchOver then handleOpponentTurn()

  def saveMatch(fileName: String): Try[_] =
    given filepath: Path = saveDirectory / fileName
    saveManager.save(logic.state)

  def loadMatch(fileName: String): Try[_] =
    given filepath: Path = saveDirectory / fileName
    val result = saveManager.load 
    result match
      case Success(matchState: MatchState) => logic = Logic(matchState)
      case _ => ()
    result

  def saveFileNames: Seq[String] = saveManager.saveFileNames

  def deleteSaveFile(fileName: String): Try[_] =
    given filepath: Path = saveDirectory / fileName
    saveManager.deleteSaveFile
