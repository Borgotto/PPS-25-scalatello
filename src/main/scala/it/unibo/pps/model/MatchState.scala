package it.unibo.pps.model

import it.unibo.pps.model.board.Board
import it.unibo.pps.model.player.*

abstract class MatchState:
  def getStatus: Any
  def getActivePlayer: Player
  def getBoard: Board
