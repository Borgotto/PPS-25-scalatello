package it.unibo.pps.view.gui.components.panels

import it.unibo.pps.view.gui.components.*

import scala.swing.*
import scala.swing.event.*

/**
 * Main menu containing a logo image and buttons to:
 * - Start a new game
 * - Load a saved game
 * - Open the settings menu
 * - Exit the application
 */
class MainMenuPanel
  (using mainFrame: ApplicationFrame)
  extends BoxPanel(Orientation.Vertical):
  
  mainFrame.title = "Scalatello - Main Menu"

  private val logo = ImagePanel(os.pwd / "src" / "main" / "resources" / "logo.jpeg")
  contents ++= logo :: Swing.VStrut(20) :: Nil

  private val btnStart = new Button("Start Game")
  private val btnLoad = new Button("Load Game")
  private val btnSettings = new Button("Settings")
  private val btnExit = new Button("Quit")

  for btn <- List(btnStart, btnLoad, btnSettings, btnExit) do
    btn.preferredSize = new Dimension(200, 50)
    btn.horizontalAlignment = Alignment.Center
    contents ++= btn :: Swing.VStrut(20) :: Nil
    listenTo(btn)

  reactions += { 
    case ButtonClicked(`btnStart`) =>
      mainFrame.matchPanel.startMatch()
      mainFrame.navigateTo(Panels.Match)
    case ButtonClicked(`btnLoad`) => mainFrame.navigateTo(Panels.LoadSave)
    case ButtonClicked(`btnSettings`) => mainFrame.navigateTo(Panels.Settings)
    case ButtonClicked(`btnExit`) => System.exit(0)
  }