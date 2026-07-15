package it.unibo.pps.controller

import it.unibo.pps.controller.save.SaveManager.SaveManagers.MatchStateSaveManager
import it.unibo.pps.model.Logic
import it.unibo.pps.observer.{Publisher, Subscriber}
import it.unibo.pps.state.MatchState
import it.unibo.pps.state.PlayerState.{Opponent, User}
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.View

import scala.annotation.tailrec

trait MatchController:
  def startMatch(boardShape: Shape, userColor: Color): Unit
  def handleSelection(position: Position): Unit
  def saveMatch(fileName: String): Unit
  def loadMatch(fileName: String): Unit
  
class MatchControllerImpl extends MatchController, Publisher[MatchState]:

  private val saveDirectory = os.home / ".scalatello"

  private var logic: Logic = _
  private var subscribers = Seq[Subscriber[MatchState]]()

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

  def saveMatch(fileName: String): Unit =
    val filePath = saveDirectory / fileName
    val saveManager = MatchStateSaveManager(filePath)
    saveManager.save(logic.state)
    notifySubscribers(logic.state)

  def loadMatch(fileName: String): Unit =
    val filePath = saveDirectory / fileName
    val saveManager = MatchStateSaveManager(filePath)
    logic = Logic(saveManager.load())
    notifySubscribers(logic.state)

object MatchController:
  def apply(view: View): MatchController =
    val controller = new MatchControllerImpl()
    controller.subscribe(view)
    controller
    