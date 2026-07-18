package it.unibo.pps.model.strategy

import it.unibo.pps.domain.{Color, Position}
import it.unibo.pps.model.board.Board
import it.unibo.pps.model.player.Opponent

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{contain, should}

import it.unibo.pps.testutils.TestExtensions.*

class OpponentPlacementStrategyTest extends AnyFlatSpec:

  "RandomOpponentPlacementStrategy" should "return a valid placement" in:
    given board: Board = """
       ....
       .WB.
       .BW.
       ....
     """.toBoard
    val opponent = Opponent.RandomOpponent(Color.White)
    val availablePlacements = board.getAvailablePlacements(opponent.color)
    val position: Position = opponent.strategy.computePlacement
    availablePlacements should contain (position)
