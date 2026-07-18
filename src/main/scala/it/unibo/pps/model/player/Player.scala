package it.unibo.pps.model.player

import it.unibo.pps.domain.Color
import it.unibo.pps.model.strategy.*

import upickle.ReadWriter

trait Player:
  def color: Color
  def strategy: UserPlacementStrategy | OpponentPlacementStrategy

case class User(color: Color) extends Player derives ReadWriter:
  val strategy: UserPlacementStrategy = UserPlacementStrategy()

enum Opponent extends Player derives ReadWriter:
  case RandomOpponent(color: Color)
  // todo: set SmartOpponent private and expose only Easy, Medium, Hard
  case SmartOpponent(color: Color, depth: Int)
  case EasyOpponent(color: Color)
  case MediumOpponent(color: Color)
  case HardOpponent(color: Color)

  val strategy: OpponentPlacementStrategy = this match
    case RandomOpponent(_) => RandomPlacementStrategy(color)
    case SmartOpponent(_, depth) => SmartPlacementStrategy(color, depth)
    case EasyOpponent(_) => SmartPlacementStrategy(color, depth = 1)
    case MediumOpponent(_) => SmartPlacementStrategy(color, depth = 3)
    case HardOpponent(_) => SmartPlacementStrategy(color, depth = 5)

object Opponent:
  def unapply(opponent: Opponent): Option[Color] = Some(opponent.color)
  