package it.unibo.pps.domain

import upickle.ReadWriter

enum ActivePlayer derives ReadWriter:
  case User
  case Opponent
