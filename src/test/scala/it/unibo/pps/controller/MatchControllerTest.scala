package it.unibo.pps.controller

import it.unibo.pps.model.LogicImpl
import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.View

import org.mockito.MockitoSugar.mock

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class MatchControllerTest extends AnyFlatSpec:
  private val view: View = mock[View]

  "A controller, to start a match" should "instantiate the correct Logic object" in:
    val boardSize = 4
    val boardShape: Shape = Shape.Square(boardSize)
    val userColor: Color = Color.Black
    val controller: MatchController = MatchControllerImpl(view)
    controller.startMatch(boardShape, userColor)
    controller.logic.state.equals(LogicImpl(boardShape, userColor).state) should be(true)

  "A controller" should "handle correctly the selected position and the opponent turn accordingly, " +
    "so the active player should be the user again" in:
    val boardSize = 4
    val boardShape: Shape = Shape.Square(boardSize)
    val userColor: Color = Color.Black
    val controller: MatchController = MatchControllerImpl(view)
    controller.startMatch(boardShape, userColor)
    controller.handleSelection(Position(boardSize/2 - 1, boardSize/2 - 1 - 1))
    controller.logic.state.activePlayer.color should be(userColor)
