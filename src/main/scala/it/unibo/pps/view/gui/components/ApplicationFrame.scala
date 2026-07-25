package it.unibo.pps.view.gui.components

import it.unibo.pps.controller.Controller
import it.unibo.pps.view.gui.components.Panels.*
import it.unibo.pps.view.gui.components.panels.*

import scala.swing.*

trait ApplicationFrame extends MainFrame:
  title = "Scalatello"
  resizable = false
  
  given mainFrame: ApplicationFrame = this
  private[gui] val controller: Controller
  private[gui] val mainMenuPanel: MainMenuPanel = MainMenuPanel()
  private[gui] val matchPanel: MatchPanel = MatchPanel()
  private[gui] val loadSavePanel: LoadSavePanel = LoadSavePanel()
  private[gui] val settingsPanel: SettingsPanel = SettingsPanel()
  
  def navigateTo(panel: Panels): Unit =
    contents = panel match
      case MainMenu => mainMenuPanel
      case Match => matchPanel
      case LoadSave => loadSavePanel
      case Settings => settingsPanel
    repaint()
    validate()
    pack()
    centerOnScreen()
    open()
