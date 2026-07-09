package it.unibo.pps.controller

import it.unibo.pps.model.{Logic, LogicImpl}
import it.unibo.pps.utils.{Color, Shape}
import it.unibo.pps.view.View

trait MatchController:
  private[controller] var logic: Logic
  def startMatch(shape: Shape, userColor: Color): Unit
  
class MatchControllerImpl(private val view: View) extends MatchController:
  private[controller] var logic: Logic = _

  def startMatch(shape: Shape, userColor: Color): Unit =
    logic = LogicImpl(shape, userColor)
    view.update(logic.state)
