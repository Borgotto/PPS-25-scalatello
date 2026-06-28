package it.unibo.pps.model

import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.Shape

trait Logic:
  val matchState: MatchState

class LogicImpl(val boardShape: Shape) extends Logic:
  override val matchState: MatchState = MatchState(boardShape)
