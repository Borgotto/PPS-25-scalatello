package it.unibo.pps.state

import it.unibo.pps.domain.{Color, Position}

import upickle.default.ReadWriter

/** Represents a specific state of a disk placed on the board.
 * 
 * @param color the color of the disk.
 * @param position the position of the disk.
 */
case class DiskState(color: Color, position: Position) derives ReadWriter
