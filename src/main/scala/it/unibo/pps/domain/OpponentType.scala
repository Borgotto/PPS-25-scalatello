package it.unibo.pps.domain

/** Represents the possible types of opponent, based on their placement strategy
 *  (hence, their skill level).
 */
enum OpponentType:
  case Erratic
  case Easy
  case Medium
  case Hard
