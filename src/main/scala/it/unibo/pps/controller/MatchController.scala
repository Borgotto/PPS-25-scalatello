package it.unibo.pps.controller

import it.unibo.pps.controller.save.SaveManager.SaveManagers.MatchStateSaveManager
import it.unibo.pps.model.Logic
import it.unibo.pps.observer.{Publisher, Subscriber}
import it.unibo.pps.state.MatchState
import it.unibo.pps.state.PlayerState.{Opponent, User}
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.View

import scala.annotation.tailrec
import scala.util.{Success, Try}
import os.Path

trait MatchController:
  def startMatch(boardShape: Shape, userColor: Color): Unit
  def handleSelection(position: Position): Unit
  def saveMatch(fileName: String): Try[_]
  def loadMatch(fileName: String): Try[_]
  def saveFileNames: Seq[String]
  def deleteSaveFile(fileName: String): Try[_]
  
class MatchControllerImpl extends MatchController, Publisher[MatchState]:

  private var logic: Logic = _
  private var subscribers = Seq[Subscriber[MatchState]]()

  private val saveDirectory = os.home / ".scalatello"
  private val saveManager = MatchStateSaveManager(saveDirectory)

  override def subscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers :+ subscriber

  override def unsubscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers.filter(s => s != subscriber)

  override def notifySubscribers(state: MatchState): Unit =
    subscribers.foreach(s => s.update(state))

  def startMatch(boardShape: Shape, userColor: Color): Unit =
    logic = Logic(boardShape, userColor)
    notifySubscribers(logic.state)
    logic.state.activePlayer match
      case User(_, _) => ()
      case Opponent(_, _) => handleOpponentTurn()

  @tailrec
  private def handleOpponentTurn(): Unit =
    logic.state.activePlayer match
      case User(_, _) => ()
      case Opponent(_, _) =>
        logic = logic.placeOpponentDisk()
        notifySubscribers(logic.state)
        handleOpponentTurn()
  
  def handleSelection(position: Position): Unit =
    logic = logic.placeUserDisk(position)
    notifySubscribers(logic.state)
    handleOpponentTurn()

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

object MatchController:
  
  def apply(view: View): MatchController =
    val controller = MatchControllerImpl()
    controller.subscribe(view)
    controller
    