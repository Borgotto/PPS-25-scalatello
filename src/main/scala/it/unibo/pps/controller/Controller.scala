package it.unibo.pps.controller

import it.unibo.pps.model.{Logic, LogicImpl}
import it.unibo.pps.utils.{Color, Shape}
import it.unibo.pps.view.View

trait Controller:
  def startMatch(boardShape: Shape, userColor: Color): Unit

class ControllerImpl(private val view: View) extends Controller:
  
  private var logic: Option[Logic] = None
  
  def startMatch(boardShape: Shape, userColor: Color): Unit =
    logic = Some(LogicImpl(boardShape, userColor))
    println(s"\nboardShape = ${boardShape.toString} \nuserColor = ${userColor.toString}")
    