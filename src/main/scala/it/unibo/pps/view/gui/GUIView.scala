package it.unibo.pps.view.gui

import it.unibo.pps.controller.Controller
import it.unibo.pps.view.View
import it.unibo.pps.view.i18n.I18n
import it.unibo.pps.view.gui.components.ApplicationFrame
import it.unibo.pps.view.gui.components.Panels
import it.unibo.pps.state.MatchState

class GUIView(using i18n: I18n) extends View with ApplicationFrame:
  val controller = Controller()
  controller.subscribe(this)
  
  override def show(): Unit =
    navigateTo(Panels.MainMenu)

  override def update(state: MatchState): Unit =
    matchPanel.update(state)
