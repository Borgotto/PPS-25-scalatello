package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.mockito.MockitoSugar.mock
import org.mockito.IdiomaticMockito.returns
import org.scalatest.matchers.should.Matchers.{should, contain}

import it.unibo.pps.model.player.Opponent
import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.Color
import it.unibo.pps.utils.Position

class OpponentPlacementStrategyTest extends AnyFlatSpec:

  "RandomOpponentPlacementStrategy" should "return a random position " in:
    // The opponent's placement strategy needs the MatchState as context
    given matchState: MatchState = mock[MatchState]
    val opponent: Opponent = Opponent.RandomOpponent(Color.White)

    matchState.getBoard returns mock[Board]
    matchState.getBoard.getAvailableMoves(opponent.color) returns Position(0, 0) :: Nil
    matchState.getActivePlayer returns opponent

    val availableMoves: List[Position] = matchState.getBoard.getAvailableMoves(opponent.color)
    val strategyResult: Position = opponent.placementStrategy.computePlacement
    availableMoves should contain (strategyResult)
