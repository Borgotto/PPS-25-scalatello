package it.unibo.pps.view.gui.components.panels

import it.unibo.pps.view.gui.components.*

import scala.swing.*
import scala.swing.event.*

/**
 * A panel that shows a list of 3 buttons to load saved games.
 * Each button corresponds to a saved game slot.
 * If a slot is empty, the button is disabled.
 */
case class LoadSavePanel()
  (using mainFrame: ApplicationFrame)
  extends BoxPanel(Orientation.Vertical):

  mainFrame.title = "Scalatello - Load Game"
  
  private lazy val saveFiles = mainFrame.controller.saveFileNames

  private val btnSlot1 = new Button("Load Slot 1")
  private val btnSlot2 = new Button("Load Slot 2")
  private val btnSlot3 = new Button("Load Slot 3")

  for btn <- List(btnSlot1, btnSlot2, btnSlot3) do
    btn.preferredSize = new Dimension(200, 50)
    contents ++= btn :: Swing.VStrut(20) :: Nil
    listenTo(btn)

  private val btnBack = new Button("Back to Main Menu")
  btnBack.preferredSize = new Dimension(300, 50)
  contents ++= btnBack :: Swing.VStrut(20) :: Nil
  listenTo(btnBack)

  reactions += {
    case ButtonClicked(`btnBack`) => mainFrame.navigateTo(Panels.MainMenu)
    case ButtonClicked(btn) =>
      println(s"Loading game from ${btn.text}")
      val slotIndex = btn match
        case `btnSlot1` => 0
        case `btnSlot2` => 1
        case `btnSlot3` => 2
      mainFrame.controller.loadMatch("save_slot_" + (slotIndex + 1) + ".json")
      mainFrame.navigateTo(Panels.Match)
  }