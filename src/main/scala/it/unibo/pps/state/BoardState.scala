package it.unibo.pps.state

import it.unibo.pps.domain.{Position, Shape}

import upickle.default.ReadWriter

case class BoardState(
  shape: Shape,
  disks: Set[DiskState],
  userAvailablePlacements: Set[Position]
) derives ReadWriter
