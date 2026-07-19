package it.unibo.pps.domain

import upickle.ReadWriter

/** Enum to represent who the active player is. */
enum ActivePlayer derives ReadWriter:
  case User
  case Opponent
