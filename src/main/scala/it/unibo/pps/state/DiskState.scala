package it.unibo.pps.state

import it.unibo.pps.utils.{Color, Position}

import upickle.default.ReadWriter

case class DiskState(color: Color, position: Position) derives ReadWriter
