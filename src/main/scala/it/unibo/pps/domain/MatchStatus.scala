package it.unibo.pps.domain

import upickle.default.ReadWriter

/** Enum to represent the possible status of a match.
 * 
 * Possible values: [[InProgress]], [[UserWon]], [[OpponentWon]], [[Tie]].
 */
enum MatchStatus derives ReadWriter:
  case InProgress
  case UserWon
  case OpponentWon
  case Tie
  