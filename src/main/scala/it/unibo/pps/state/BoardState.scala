package it.unibo.pps.state

import it.unibo.pps.domain.{Position, Shape}

import upickle.default.ReadWriter

/** Represents a specific state of the board.
 * 
 * @param shape the shape of the board.
 * @param disks the state of the disks placed on the board.
 * @param userAvailablePlacements the positions on which the user can place
 *                                their next disk performing a valid placement.
 */
case class BoardState(
  shape: Shape,
  disks: Set[DiskState],
  userAvailablePlacements: Set[Position]
) derives ReadWriter
