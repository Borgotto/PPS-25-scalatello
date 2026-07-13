package it.unibo.pps.utils

import upickle.default.ReadWriter

case class Position(row: Int, column: Int) derives ReadWriter
