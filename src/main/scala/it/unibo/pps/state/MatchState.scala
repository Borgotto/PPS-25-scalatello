package it.unibo.pps.state

import it.unibo.pps.domain.{ActivePlayer, MatchStatus}
import it.unibo.pps.model.player.{Opponent, User}

import upickle.default.ReadWriter

/** Represents a specific state of a match.
 * 
 * @param status the status of the match (i.e. whether the match is in progress
 *               or its final result if it is not).
 * @param user the information about the user in the match (i.e. their color).
 * @param opponent the information about the opponent in the match (i.e. 
 *                 their color and placement strategy).
 * @param activePlayer the player that must perform a placement.
 * @param board the state of the board.
 */
case class MatchState(
  status: MatchStatus,
  user: User,
  opponent: Opponent,
  activePlayer: ActivePlayer,
  board: BoardState
) derives ReadWriter
