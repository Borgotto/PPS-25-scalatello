package it.unibo.pps.view

import it.unibo.pps.observer.Subscriber
import it.unibo.pps.state.MatchState
import it.unibo.pps.view.i18n.I18n

trait View(i18n: I18n) extends Subscriber[MatchState]:
  def showMenu(): Unit
