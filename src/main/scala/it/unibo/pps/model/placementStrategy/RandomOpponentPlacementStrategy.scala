package it.unibo.pps.model.placementStrategy

import scala.util.Random

import it.unibo.pps.model.MatchState
import it.unibo.pps.utils.Position

class RandomOpponentPlacementStrategy extends OpponentPlacementStrategy:
  override def computePlacement(using matchState: MatchState): Position =
    Position(0, 0) // todo: implement strategy