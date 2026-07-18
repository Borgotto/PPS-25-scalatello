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
  val strategy: OpponentPlacementStrategy = this match
    case RandomOpponent(_) => RandomPlacementStrategy(color)

object Opponent:
  def unapply(opponent: Opponent): Option[Color] = Some(opponent.color)