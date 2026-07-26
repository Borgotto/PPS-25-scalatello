package it.unibo.pps.domain

import upickle.default.ReadWriter

/** Represents the possible status of a match (i.e. whether the match is in progress
 *  or its final result if it is not). 
 */
enum MatchStatus derives ReadWriter:
  case InProgress
  case UserWon
  case OpponentWon
  case Tie
  