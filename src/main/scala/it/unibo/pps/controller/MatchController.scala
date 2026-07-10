package it.unibo.pps.controller

import it.unibo.pps.model.board.Board
import it.unibo.pps.model.{Logic, LogicImpl}
import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.View

trait MatchController:
  private[controller] var logic: Logic
  def startMatch(shape: Shape, userColor: Color): Unit
  def resumeMatch(userColor: Color, board: Board): Unit
  def handleSelection(diskPos: Position): Unit
  def saveMatch(filePath: String): Unit
  def loadMatch(filePath: String): Unit
  
class MatchControllerImpl(private val view: View) extends MatchController:
  private[controller] var logic: Logic = _

  private def handleOpponentTurn(userColor: Color): Unit =
    while
      logic.state.activePlayer.color.equals(userColor.opposite)
    do
      logic = logic.placeOpponentDisk()
      view.update(logic.state)

  def startMatch(shape: Shape, userColor: Color): Unit =
    logic = LogicImpl(shape, userColor)
    
    logic.state.activePlayer.color match
      case c if c.equals(userColor.opposite) =>
        handleOpponentTurn(userColor)
      case _ => view.update(logic.state)
      
  def resumeMatch(userColor: Color, board: Board): Unit =
    logic = LogicImpl(userColor, board)
    view.update(logic.state)
  
  def handleSelection(diskPos: Position): Unit =
    val userColor = logic.state.activePlayer.color
    logic = logic.placeUserDisk(diskPos)
    view.update(logic.state)
    handleOpponentTurn(userColor)

  def saveMatch(filePath: String): Unit =
    val saveManager: SaveManager = SaveManagerImpl(filePath)
    saveManager.save(logic.state)

  def loadMatch(filePath: String): Unit =
    val saveManager: SaveManager = SaveManagerImpl(filePath)
    view.update(saveManager.load())
    
