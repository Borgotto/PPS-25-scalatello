package it.unibo.pps.controller

import it.unibo.pps.model.{Logic, LogicImpl}
import it.unibo.pps.observer.{Publisher, Subscriber}
import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.View

trait Controller:
  def startMatch(boardShape: Shape, userColor: Color): Unit
  def handleSelection(position: Position): Unit

class ControllerImpl extends Controller, Publisher[MatchState]:

  private var logic: Logic = _
  private var subscribers = Seq[Subscriber[MatchState]]()

  override def subscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers :+ subscriber

  override def unsubscribe(subscriber: Subscriber[MatchState]): Unit =
    subscribers = subscribers.filter(s => s != subscriber)

  override def notifySubscribers(state: MatchState): Unit =
    subscribers.foreach(s => s.update(state))

  def startMatch(boardShape: Shape, userColor: Color): Unit =
    logic = LogicImpl(boardShape, userColor)
    notifySubscribers(logic.state)

  def handleSelection(position: Position): Unit =
    println(s"\nSelected position: $position")

object ControllerImpl:

  def apply(view: View): ControllerImpl =
    val controller = new ControllerImpl()
    controller.subscribe(view)
    controller
    