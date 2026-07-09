package it.unibo.pps.controller

import it.unibo.pps.model.LogicImpl
import it.unibo.pps.utils.{Color, Shape}
import it.unibo.pps.view.View

import org.mockito.MockitoSugar.mock

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class MatchControllerTest extends AnyFlatSpec:
  private val view: View = mock[View]
  private val controller: MatchController = MatchControllerImpl(view)

  "A controller, to start a match" should "instantiate the correct Logic object" in:
    val boardSize = 4
    val boardShape: Shape = Shape.Square(boardSize)
    val userColor: Color = Color.Black
    controller.startMatch(boardShape, userColor)
    controller.logic.state.equals(LogicImpl(boardShape, userColor).state) should be(true)
