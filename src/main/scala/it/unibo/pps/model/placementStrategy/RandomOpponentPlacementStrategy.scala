package it.unibo.pps.model.placementStrategy

import scala.util.Random

import it.unibo.pps.utils.Position
import it.unibo.pps.model.MatchState

class RandomOpponentPlacementStrategy extends OpponentPlacementStrategy:
  def computePlacement(using matchState: MatchState): Position =
    val color = matchState.getActivePlayer.color
    val board = matchState.getBoard
    val availableMoves = board.getAvailableMoves(color)
    val randomIndex = Random.nextInt(availableMoves.length)
    availableMoves(randomIndex)