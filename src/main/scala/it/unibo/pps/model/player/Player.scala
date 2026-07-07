package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.strategy.*
import it.unibo.pps.state.PlayerState

trait Player:
  def color: Color
  def strategy: UserPlacementStrategy | OpponentPlacementStrategy
  def state: PlayerState

case class User(color: Color) extends Player:
  override val strategy: UserPlacementStrategy = UserPlacementStrategy()
  override val state = PlayerState.User(color)

enum Opponent extends Player:
  case RandomOpponent(color: Color)
  
  override val strategy: OpponentPlacementStrategy = this match
    case RandomOpponent(_) => RandomPlacementStrategy(color)
    
  override val state = PlayerState.Opponent(color)
  
object Opponent:
  def unapply(opponent: Opponent): Option[Color] = Some(opponent.color)