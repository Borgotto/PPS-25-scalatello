package it.unibo.pps.view

import it.unibo.pps.state.MatchState

trait View:
  def update(state: MatchState): Unit
