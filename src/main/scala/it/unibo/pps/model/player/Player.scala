package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.*

trait Player:
  def color: Color
  def placementStrategy: UserPlacementStrategy | OpponentPlacementStrategy

case class User(color: Color) extends Player:
  def placementStrategy: UserPlacementStrategy = UserPlacementStrategy()

enum Opponent extends Player:
  case RandomOpponent(color: Color)
  def placementStrategy: OpponentPlacementStrategy =
    this match
      case RandomOpponent(_) => RandomOpponentPlacementStrategy()