package it.unibo.pps.controller

import it.unibo.pps.model.LogicImpl
import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.{Color, Position, Shape}
import it.unibo.pps.view.View

import org.mockito.MockitoSugar.mock

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class MatchControllerTest extends AnyFlatSpec:
  private val BOARD_SIZE = 4
  private val BOARD_SHAPE: Shape = Shape.Square(BOARD_SIZE)
  private val USER_COLOR: Color = Color.Black

  private val topLeftCenterPos = Position(BOARD_SIZE/2 - 1, BOARD_SIZE/2 - 1)
  private val dist = 1
  private val validPos = Position(topLeftCenterPos.row, topLeftCenterPos.column - dist)

  private val view: View = mock[View]
  private val controller: MatchController = MatchControllerImpl(view)
  controller.startMatch(BOARD_SHAPE, USER_COLOR)

  "A controller, to start a match" should "instantiate the correct Logic object" in:
    controller.logic.state.equals(LogicImpl(BOARD_SHAPE, USER_COLOR).state) should be(true)

  "A controller, if the first player is the opponent" should "immediately handle its turn, " +
    "so then the active player should be the user" in:
    val controllerOpponent: MatchController = MatchControllerImpl(view)
    val userColor: Color = Color.White
    controllerOpponent.startMatch(BOARD_SHAPE, userColor)
    controllerOpponent.logic.state.activePlayer.color.equals(userColor) should be(true)

  "A controller" should "handle correctly the selected position and the opponent turn accordingly, " +
    "so the active player should be the user again" in:
    controller.handleSelection(validPos)
    controller.logic.state.activePlayer.color should be(USER_COLOR)
