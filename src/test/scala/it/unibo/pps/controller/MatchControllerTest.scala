package it.unibo.pps.controller

import it.unibo.pps.controller.matchController.{MatchController, MatchControllerImpl}
import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.model.{Logic, LogicImpl}
import it.unibo.pps.model.strategy.UserPlacementStrategy
import it.unibo.pps.state.{BoardState, DiskState, MatchState, PlayerState}
import it.unibo.pps.utils.{Color, MatchStatus, Position, Shape}
import it.unibo.pps.view.View

import org.mockito.MockitoSugar.mock

import org.scalatest.PrivateMethodTester
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{be, should}

class MatchControllerTest extends AnyFlatSpec with PrivateMethodTester:
  private val BOARD_SIZE = 4
  private val BOARD_SHAPE: Shape = Shape.Square(BOARD_SIZE)
  private val USER_COLOR: Color = Color.Black

  private val topLeftCenterPos = Position(BOARD_SIZE/2 - 1, BOARD_SIZE/2 - 1)
  private val dist = 1

  private val view: View = mock[View]
  private val controller: MatchController = MatchControllerImpl(view)
  controller.startMatch(BOARD_SHAPE, USER_COLOR)

  extension (s: MatchState)
    private def equalsToState(state: MatchState): Boolean =
      s.status.equals(state.status) &&
        s.activePlayer.color.equals(state.activePlayer.color) &&
        s.activePlayer.strategy.equals(state.activePlayer.strategy) &&
        s.board.shape.equals(state.board.shape) &&
        s.board.disks.toSet.equals(state.board.disks.toSet) &&
        s.board.userAvailablePlacements.equals(state.board.userAvailablePlacements)

  "A Controller, to start a match" should "instantiate the correct Logic object" in:
    controller.logic.state.equalsToState(LogicImpl(BOARD_SHAPE, USER_COLOR).state) should be(true)

  "A Controller, if the first player is the opponent" should "handle its turn, " +
    "instantiating the Logic object accordingly" in:
    val controllerOpponent: MatchController = MatchControllerImpl(view)
    val userColor: Color = Color.White
    controllerOpponent.logic = LogicImpl(userColor,
      Board(BOARD_SHAPE, Map(
          Position(topLeftCenterPos.row - dist, topLeftCenterPos.column) -> Disk(Color.White),
          topLeftCenterPos -> Disk(Color.White),
          Position(topLeftCenterPos.row, topLeftCenterPos.column + dist) -> Disk(Color.Black)
        )
      )
    )
    val expectedState: MatchState =
      MatchState(MatchStatus.InProgress,
        PlayerState.User(userColor, UserPlacementStrategy()),
        BoardState(BOARD_SHAPE, Seq(
            DiskState(Color.White, Position(topLeftCenterPos.row - dist, topLeftCenterPos.column)),
            DiskState(Color.Black, topLeftCenterPos),
            DiskState(Color.Black,  Position(topLeftCenterPos.row, topLeftCenterPos.column + dist)),
            DiskState(Color.Black, Position(topLeftCenterPos.row, topLeftCenterPos.column - dist))
          ), Set(
            Position(topLeftCenterPos.row + dist, topLeftCenterPos.column),
            Position(topLeftCenterPos.row + dist, topLeftCenterPos.column + dist + dist)
          )
        )
      )
    val handleOpponentTurn = PrivateMethod[Unit](Symbol("handleOpponentTurn"))
    controllerOpponent invokePrivate handleOpponentTurn(userColor)
    controllerOpponent.logic.state.equalsToState(expectedState) should be(true)

  "A Controller" should "handle correctly the selected position and the opponent turn, " +
    "instantiating the Logic object accordingly" in:
    val expectedState: MatchState =
      MatchState(MatchStatus.InProgress,
        PlayerState.User(USER_COLOR, UserPlacementStrategy()),
        BoardState(BOARD_SHAPE,
          Seq(
            DiskState(Color.White, Position(topLeftCenterPos.row - dist, topLeftCenterPos.column - dist)),
            DiskState(Color.White, topLeftCenterPos),
            DiskState(Color.Black, Position(topLeftCenterPos.row, topLeftCenterPos.column + dist)),
            DiskState(Color.Black, Position(topLeftCenterPos.row, topLeftCenterPos.column + dist + dist)),
            DiskState(Color.White, Position(topLeftCenterPos.row + dist, topLeftCenterPos.column + dist))
          ), Set(
            Position(topLeftCenterPos.row, topLeftCenterPos.column - dist),
            Position(topLeftCenterPos.row + dist + dist, topLeftCenterPos.column),
            Position(topLeftCenterPos.row + dist + dist, topLeftCenterPos.column + dist)
          )
        )
      )
    controller.logic = LogicImpl(USER_COLOR,
      Board(BOARD_SHAPE, Map(
          Position(topLeftCenterPos.row - dist, topLeftCenterPos.column - dist) -> Disk(Color.White),
          Position(topLeftCenterPos.row, topLeftCenterPos.column + dist) -> Disk(Color.White),
          Position(topLeftCenterPos.row, topLeftCenterPos.column + dist + dist) -> Disk(Color.Black)
        )
      )
    )
    println(controller.logic.state)
    val validPos = Position(topLeftCenterPos.row, topLeftCenterPos.column)
    controller.handleSelection(validPos)
    println(controller.logic.state)
    println(expectedState)
    controller.logic.state.equalsToState(expectedState) should be(true)

  "A Controller" should "instantiate the correct Logic object from a MatchState" in:
    val stateToLogic = PrivateMethod[Logic](Symbol("stateToLogic"))
    val expectedState: MatchState =
      MatchState(MatchStatus.InProgress,
        PlayerState.User(USER_COLOR, UserPlacementStrategy()),
        BoardState(BOARD_SHAPE, Seq(DiskState(Color.Black, Position(0, 0))), Set())
      )
    val logic: Logic = controller invokePrivate stateToLogic(expectedState)
    logic.state.equalsToState(expectedState) should be(true)