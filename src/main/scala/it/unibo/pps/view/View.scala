package it.unibo.pps.view

import it.unibo.pps.state.MatchState
import it.unibo.pps.view.i18n.I18n
import it.unibo.pps.view.observer.Subscriber

trait View(i18n: I18n) extends Subscriber[MatchState]:
  def showMenu(): Unit
