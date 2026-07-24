package it.unibo.pps.controller

import it.unibo.pps.controller.save.SaveManager.SaveManagers.MatchStateSaveManager
import it.unibo.pps.domain.{Color, OpponentType, Position, Shape, ActivePlayer, MatchStatus}
import it.unibo.pps.model.Logic
import it.unibo.pps.observer.{Publisher, Subscriber}
import it.unibo.pps.state.MatchState

import scala.annotation.tailrec
import scala.util.{Success, Try}
import os.Path

/** Defines the main controller of the application.
 *
 *  All methods must be specified by every class using it.
 *
 *  Used by: [[ControllerImpl]]
 */
trait Controller extends Publisher[MatchState]:
  /** Starts and configures a match according to the given settings.
   *  @param boardShape the [[Shape]] the [[Board]] must have.
   *  @param userColor the [[Color]] assigned to the user.
   *  @param opponentType the type of opponent.
   */
  def startMatch(boardShape: Shape, userColor: Color, opponentType: OpponentType): Unit

  /** Given the position selected by the user, handles their turn.
   *  @param position the position selected by the user.
   */
  def handleSelection(position: Position): Unit

  /** Saves the current state of the match.
   *  @param fileName the name of the save file.
   *  @return [[scala.util.Success]] if the save is successful, [[scala.util.Failure]] otherwise.
   */
  def saveMatch(fileName: String): Try[_]

  /** Loads a match from a save file.
   *  @param fileName the name of the save file from which the match must be loaded.
   *  @return [[scala.util.Success]] if the match is loaded correctly, [[scala.util.Failure]] otherwise.
   */
  def loadMatch(fileName: String): Try[MatchState]

  /** @return the names of the save files. */
  def saveFileNames: Seq[String]

  /** Deletes a save file.
   *  @param fileName the name of the save file to delete.
   *  @return [[scala.util.Success]] if the file is deleted successfully, [[scala.util.Failure]] otherwise.
   */
  def deleteSaveFile(fileName: String): Try[_]

/** Factory for [[Controller]] instances. */
object Controller:
  /** Instantiates the controller of the application. */
  def apply(): Controller = ControllerImpl()

/** Implements the controller of the application. */
private[controller] class ControllerImpl extends Controller:
  private var logic: Option[Logic] = Option.empty
  private var subscribers = Seq[Subscriber[MatchState]]()

  private val saveDirectory = os.home / ".scalatello"
  private val saveManager = MatchStateSaveManager(saveDirectory)
  
  def subscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers :+ subscriber
  
  def unsubscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers.filter(s => s != subscriber)
  
  def notifySubscribers(state: MatchState): Unit =
    subscribers.foreach(s => s.update(state))

  private def isMatchOver: Boolean = logic.get.state.status != MatchStatus.InProgress

  @tailrec
  private def handleOpponentTurn(): Unit =
    notifySubscribers(logic.get.state)
    logic.get.state.activePlayer match
      case ActivePlayer.Opponent if !isMatchOver =>
        logic = Some(logic.get.placeOpponentDisk())
        handleOpponentTurn()
      case _ => ()

  def startMatch(boardShape: Shape, userColor: Color, opponentType: OpponentType): Unit =
    logic = Some(Logic(boardShape, userColor, opponentType))
    logic.get.state.activePlayer match
      case ActivePlayer.Opponent => handleOpponentTurn()
      case _ => notifySubscribers(logic.get.state)

  /** Given the position selected by the user, handles their turn. Then handles the available opponent's turns.
   *  @param position the position selected by the user.
   */
  def handleSelection(position: Position): Unit =
    logic match
      case Some(currentLogic) =>
        logic = Some(currentLogic.placeUserDisk(position))
        handleOpponentTurn()
      case None => ()

  /** @inheritdoc
   *  @param fileName the name of the save file.
   *  @return [[scala.util.Success]] if the save is successful, [[scala.util.Failure]] otherwise.
   *  @throws java.lang.IllegalStateException if there is no match to save.
   */
  def saveMatch(fileName: String): Try[_] =
    logic match
      case Some(currentLogic) =>
        given filepath: Path = saveDirectory / fileName
        saveManager.save(logic.get.state)
      case None => throw IllegalStateException("There is no match to save")

  def loadMatch(fileName: String): Try[MatchState] =
    given filepath: Path = saveDirectory / fileName
    val result = saveManager.load 
    result match
      case Success(matchState: MatchState) => logic = Some(Logic(matchState))
      case _ => ()
    result

  def saveFileNames: Seq[String] = saveManager.saveFileNames

  def deleteSaveFile(fileName: String): Try[_] =
    given filepath: Path = saveDirectory / fileName
    saveManager.deleteSaveFile
