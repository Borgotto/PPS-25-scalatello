package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.strategy.*
import it.unibo.pps.state.PlayerState

trait Player:
  def color: Color
  def strategy: UserPlacementStrategy | OpponentPlacementStrategy
  def state: PlayerState

case class User(color: Color) extends Player:
  val strategy: UserPlacementStrategy = UserPlacementStrategy()
  val state = PlayerState.User(color, strategy)

enum Opponent extends Player:
  case RandomOpponent(color: Color)
  // todo: set SmartOpponent private and expose only Easy, Medium, Hard
  case SmartOpponent(color: Color, depth: Int)
  case EasyOpponent(color: Color)
  case MediumOpponent(color: Color)
  case HardOpponent(color: Color)

  val strategy: OpponentPlacementStrategy = this match
    case RandomOpponent(_) => RandomPlacementStrategy(color)
    case SmartOpponent(_, depth) => SmartPlacementStrategy(color, depth)
    case EasyOpponent(_) => SmartPlacementStrategy(color, depth=1)
    case MediumOpponent(_) => SmartPlacementStrategy(color, depth=3)
    case HardOpponent(_) => SmartPlacementStrategy(color, depth=5)

  val state = PlayerState.Opponent(color, strategy)

object Opponent:
  def unapply(opponent: Opponent): Option[Color] = Some(opponent.color)