package it.unibo.pps.view.gui.components.dialogs

import it.unibo.pps.view.gui.components.*
import it.unibo.pps.state.MatchState
import it.unibo.pps.domain.MatchStatus

import scala.swing.*
import scala.swing.event.*

case class MatchStatusDialog(matchState: MatchState)
  (using mainFrame: ApplicationFrame)
  extends Dialog(mainFrame):
  
  private val lblStatus = new Label(matchState.status match
    case MatchStatus.UserWon => "You won!"
    case MatchStatus.OpponentWon => "You lost!"
    case MatchStatus.Tie => "It's a tie!"
    case _ => ""
  )
  
  private val btnPlayAgain = new Button("Play Again")
  private val btnMainMenu = new Button("Main Menu")
  private val btnClose = new Button("Quit")
  listenTo(btnPlayAgain, btnMainMenu, btnClose)
  
  reactions += {
    case ButtonClicked(`btnPlayAgain`) =>
      mainFrame.matchPanel.startMatch()
      mainFrame.navigateTo(Panels.Match)
      close()
    case ButtonClicked(`btnMainMenu`) =>
      mainFrame.navigateTo(Panels.MainMenu)
      close()
    case ButtonClicked(`btnClose`) =>
      sys.exit(0)
  }

  private val boxPanel = new BoxPanel(Orientation.Vertical):
    contents ++= Seq(
      Swing.VStrut(20),
      lblStatus,
      Swing.VStrut(10),
      btnPlayAgain,
      Swing.VStrut(10),
      btnMainMenu,
      Swing.VStrut(10),
      btnClose,
      Swing.VStrut(20)
    )

  title = "Match Ended"
  modal = true
  contents = boxPanel
  setLocationRelativeTo(mainFrame)  
