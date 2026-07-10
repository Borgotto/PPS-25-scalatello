package it.unibo.pps.controller

import it.unibo.pps.model.{Logic, LogicImpl}
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.View

trait MatchController:
  private[controller] var logic: Logic
  def startMatch(shape: Shape, userColor: Color): Unit
  def handleSelection(diskPos: Position): Unit
  
class MatchControllerImpl(private val view: View) extends MatchController:
  private[controller] var logic: Logic = _

  def startMatch(shape: Shape, userColor: Color): Unit =
    logic = LogicImpl(shape, userColor)
    
    logic.state.activePlayer.color match
      case c if c.equals(userColor.opposite) =>
        while
          logic.state.activePlayer.color.equals(userColor.opposite)
        do
          logic = logic.placeOpponentDisk()
          view.update(logic.state)
      case _ => view.update(logic.state)

  def handleSelection(diskPos: Position): Unit =
    val userColor = logic.state.activePlayer.color
    logic = logic.placeUserDisk(diskPos)
    view.update(logic.state)
    while
      logic.state.activePlayer.color.equals(userColor.opposite)
    do
      logic = logic.placeOpponentDisk()
      view.update(logic.state)
