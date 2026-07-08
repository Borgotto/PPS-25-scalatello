package it.unibo.pps.view

import it.unibo.pps.state.MatchState
import it.unibo.pps.view.i18n.I18n

class CLIView(private val i18n: I18n) extends View(i18n):

  override def showMenu(): Unit =
    println(i18n.t("menu.welcome_message"))

  override def update(state: MatchState): Unit = ???
