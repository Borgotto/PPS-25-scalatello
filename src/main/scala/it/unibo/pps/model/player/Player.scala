package it.unibo.pps.model.player

import it.unibo.pps.domain.Color
import it.unibo.pps.model.strategy.*

import upickle.ReadWriter

/**
 * Base trait defining the interface for a player in the game.
 *
 * A player has a [[Color]] and a [[PlacementStrategy]].
 * This trait acts as a common abstraction for both human users and AI opponents.
 */
trait Player:
  /**
   * @return the Color associated with this player
   */
  def color: Color

  /**
   * The placement strategy used by this player to compute moves.
   *
   * This can be either a [[UserPlacementStrategy]] or an [[OpponentPlacementStrategy]]
   * depending on whether the player is human or AI-controlled.
   *
   * @return [[UserPlacementStrategy]] | [[OpponentPlacementStrategy]]
   */
  def strategy: UserPlacementStrategy | OpponentPlacementStrategy

/**
 * Represents a human player controlled by user input.
 *
 * The user player uses a UserPlacementStrategy to accept positions directly from the user.
 *
 * @param color the color assigned to this user player
 * @note This class derives a uPickle ReadWriter for serialization support.
 */
case class User(color: Color) extends Player derives ReadWriter:
  val strategy: UserPlacementStrategy = UserPlacementStrategy()

/**
 * Represents an AI-controlled opponent player with configurable difficulty levels.
 */
enum Opponent extends Player derives ReadWriter:
  /**
   * A random opponent that chooses random placements between available options
   *
   * @param color the color assigned to this opponent
   */
  case RandomOpponent(color: Color)

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
   * @return an OpponentPlacementStrategy appropriate for this opponent's difficulty level
   */
  val strategy: OpponentPlacementStrategy = this match
    case RandomOpponent(_) => RandomPlacementStrategy(color)
    case EasyOpponent(_) => SmartPlacementStrategy(color, depth = 1)
    case MediumOpponent(_) => SmartPlacementStrategy(color, depth = 3)
    case HardOpponent(_) => SmartPlacementStrategy(color, depth = 5)

object Opponent:
  def unapply(opponent: Opponent): Option[Color] = Some(opponent.color)
