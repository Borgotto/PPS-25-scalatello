package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.mockito.MockitoSugar.mock
import org.mockito.IdiomaticMockito.returns

import it.unibo.pps.model.placementStrategy.*
import it.unibo.pps.model.player.*
import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.Color
import it.unibo.pps.utils.Position

class OpponentPlacementStrategyTest extends AnyFlatSpec:

  "RandomOpponentPlacementStrategy" should "return a random position " in:
    // The opponent's placement strategy needs the MatchState as context
    given matchState: MatchState = mock[MatchState]
    val opponent: Opponent = Opponent.RandomOpponent
    
    matchState.getBoard returns mock[Board]
    matchState.getBoard.getAvailableMoves(opponent.color) returns List(Position(0, 0))
    matchState.getActivePlayer returns opponent

    val availableMoves: List[Position] = matchState.getBoard.getAvailableMoves(opponent.color)
    val strategyResult: Position = opponent.getPlacementStrategy.computePlacement
    assert(availableMoves contains strategyResult)
