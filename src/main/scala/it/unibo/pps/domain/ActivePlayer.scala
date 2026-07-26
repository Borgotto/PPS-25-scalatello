package it.unibo.pps.domain

import upickle.ReadWriter

/** Represents who the active player is. */
enum ActivePlayer derives ReadWriter:
  case User
  case Opponent
  
  /** @return the other player. */
  def next: ActivePlayer = this match
    case User => Opponent
    case Opponent => User
