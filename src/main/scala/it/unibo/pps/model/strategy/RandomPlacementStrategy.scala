package it.unibo.pps.model.strategy

import scala.util.Random
import it.unibo.pps.state.{BoardState, MatchState}
import it.unibo.pps.utils.Position

case class RandomPlacementStrategy() extends PlacementStrategy[BoardState, Position]:
  override def computePlacement(using boardState: BoardState): Position =
    val availablePlacements = boardState.availablePlacements
    val randomIndex = Random.nextInt(availablePlacements.length)
    availablePlacements(randomIndex)
