package it.unibo.pps.controller

import it.unibo.pps.domain.{Color, Position, Shape}
import it.unibo.pps.state.MatchState

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify}

import org.scalatest.flatspec.AnyFlatSpec

class MatchControllerTest extends AnyFlatSpec:
  private val BOARD_SIZE = 4
  private val BOARD_SHAPE: Shape = Shape.Square(BOARD_SIZE)

  private val topLeftCenterPos: Position = Position(BOARD_SIZE / 2 - 1, BOARD_SIZE / 2 - 1)
  private val validPos: Position = Position(topLeftCenterPos.row, topLeftCenterPos.column - 1)

  "A Controller, if the first player is the User" should "notify the state of the match only once" in:
    val mockedController: MatchControllerImpl = Mockito.spy(MatchControllerImpl())
    mockedController.startMatch(BOARD_SHAPE, Color.Black)
    verify(mockedController, times(1)).notifySubscribers(any(classOf[MatchState]))

  "A Controller, if the first player is the opponent" should "notify the state of the match twice" in:
    val mockedController: MatchControllerImpl = Mockito.spy(MatchControllerImpl())
    mockedController.startMatch(BOARD_SHAPE, Color.White)
    verify(mockedController, times(2)).notifySubscribers(any(classOf[MatchState]))

  "A Controller" should "notify the state of the match thrice, at the start, after user selection and opponent turn" in:
    val mockedController: MatchControllerImpl = Mockito.spy(MatchControllerImpl())
    mockedController.startMatch(BOARD_SHAPE, Color.Black)
    mockedController.handleSelection(validPos)
    verify(mockedController, times(3)).notifySubscribers(any(classOf[MatchState]))
