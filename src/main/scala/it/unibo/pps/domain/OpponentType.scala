package it.unibo.pps.domain

/** Represents the possible types of opponent, based on their placement strategy
 *  (hence, their skill level).
 */
enum OpponentType:
  case Random
  case Easy
  case Medium
  case Hard
