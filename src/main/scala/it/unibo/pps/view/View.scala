package it.unibo.pps.view

import it.unibo.pps.observer.Subscriber
import it.unibo.pps.state.MatchState

trait View extends Subscriber[MatchState]:
  def show(): Unit
