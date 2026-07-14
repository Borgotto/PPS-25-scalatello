package it.unibo.pps.utils

import upickle.default.ReadWriter

enum MatchStatus derives ReadWriter:
  case InProgress
  case UserWon
  case OpponentWon
  case Tie
  