package it.unibo.pps.model

import it.unibo.pps.model.board.Board
import it.unibo.pps.model.strategy.{PlacementStrategy, RandomPlacementStrategy, UserPlacementStrategy}
import it.unibo.pps.state.PlayerState
import it.unibo.pps.utils.{Color, Position}

trait Player:
  type Input
  def color: Color
  def strategy: PlacementStrategy[Input, Position]
  def state: PlayerState

case class User(color: Color) extends Player:
  override type Input = Position
  override def strategy: UserPlacementStrategy = UserPlacementStrategy()
  override def state = PlayerState.User(color)

case class Opponent(color: Color) extends Player:
  override type Input = Board
  override def strategy: PlacementStrategy[Board, Position] = RandomPlacementStrategy(color)
  override def state = PlayerState.Opponent(color)

object RandomOpponent:
  def apply(color: Color): Opponent = Opponent(color)
  