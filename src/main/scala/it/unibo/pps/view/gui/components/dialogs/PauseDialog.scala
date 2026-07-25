package it.unibo.pps.view.gui.components.dialogs

import it.unibo.pps.view.gui.components.*

import scala.swing.*
import scala.swing.event.*

case class PauseDialog()
  (using mainFrame: ApplicationFrame)
  extends Dialog(mainFrame):

  private val btnResume = new Button("Resume")
  private val btnSave = new Button("Save Game")
  private val btnMainMenu = new Button("Main Menu")
  listenTo(btnResume, btnSave, btnMainMenu)

  reactions += {
    case ButtonClicked(`btnResume`) =>
      close()
    case ButtonClicked(`btnSave`) =>
      mainFrame.controller.saveMatch("save_slot_1.json") // TODO: implement a proper save slot selection
      close()
    case ButtonClicked(`btnMainMenu`) =>
      mainFrame.navigateTo(Panels.MainMenu)
      close()
  }
  
  private val boxPanel = new BoxPanel(Orientation.Vertical):
    contents ++= Seq(
      Swing.VStrut(20),
      btnResume,
      Swing.VStrut(10),
      btnSave,
      Swing.VStrut(10),
      btnMainMenu,
      Swing.VStrut(20)
    )
  
  title = "Match Paused"
  modal = true
  contents = boxPanel