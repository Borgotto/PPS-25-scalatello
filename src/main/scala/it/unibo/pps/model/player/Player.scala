package it.unibo.pps.model.player

import it.unibo.pps.domain.Color
import it.unibo.pps.model.player.strategy.{ErraticPlacementStrategy, OpponentPlacementStrategy, SmartPlacementStrategy}
import upickle.ReadWriter

/**
 * Base trait defining the interface for a player in the game.
 *
 * A player has a [[domain.Color]] and a [[strategy.PlacementStrategy]].
 * This trait acts as a common abstraction for both human users and AI opponents.
 */
trait Player:
  /**
   * @return the [[domain.Color]] associated with this player
   */
  def color: Color

/**
 * Represents a human player controlled by user input.
 *
 * @param color the color assigned to this user player
 * @note This class derives a uPickle ReadWriter for serialization support.
 */
case class User(color: Color) extends Player derives ReadWriter

/**
 * Represents an AI-controlled opponent player with configurable difficulty levels.
 */
enum Opponent extends Player derives ReadWriter:
  /**
   * An erratic opponent that chooses random placements between available options
   *
   * @param color the color assigned to this opponent
   */
  case ErraticOpponent(color: Color)

  /**
   * An opponent whose strategy is to place disks where it gains the most immediate advantage
   *
   * @param color the color assigned to this opponent
   */
  case EasyOpponent(color: Color)

  /**
   * A medium-difficulty opponent with basic lookahead capability.
   *
   * @param color the color assigned to this opponent
   */
  case MediumOpponent(color: Color)

  /**
   * A hard-difficulty opponent with deeper lookahead capabilities.
   *
   * @param color the color assigned to this opponent
   */
  case HardOpponent(color: Color)

  /**
   * The placement strategy for this opponent, determined by its difficulty level.
   *
   * @return an [[player.strategy.OpponentPlacementStrategy]] appropriate for this opponent's difficulty level
   */
  val strategy: OpponentPlacementStrategy = this match
    case ErraticOpponent(_) => ErraticPlacementStrategy(color)
    case EasyOpponent(_) => SmartPlacementStrategy(color, depth = 1)
    case MediumOpponent(_) => SmartPlacementStrategy(color, depth = 2)
    case HardOpponent(_) => SmartPlacementStrategy(color, depth = 4)

object Opponent:
  def unapply(opponent: Opponent): Option[Color] = Some(opponent.color)
