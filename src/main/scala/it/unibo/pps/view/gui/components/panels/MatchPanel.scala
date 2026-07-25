package it.unibo.pps.view.gui.components.panels

import it.unibo.pps.domain.*
import it.unibo.pps.state.*
import it.unibo.pps.view.gui.components.*
import it.unibo.pps.view.gui.theme.GUIColor

import java.awt.Color as JColor
import scala.swing.{Color as SwingColor, *}
import scala.swing.event.*

/**
 * A panel that shows the game board and the current match status.
 * It contains:
 * - A row of labels showing the current number of disks for each player, with the corresponding color.
 * - A row containing a button to open a popup to save the game or quit, and then a label to the right that says "Your turn" or "Opponent's turn" depending on the current player.
 * - A grid of buttons representing the game board, with a green background and a black border
 */
class MatchPanel
  (using mainFrame: ApplicationFrame)
  extends BoxPanel(Orientation.Vertical):
  
  mainFrame.title = "Scalatello - Game"  

  private val player1Label = new Label()
  private val player2Label = new Label()
  private val turnLabel = new Label()
  private val pauseButton = new Button("Pause")
  private val pauseDialog = dialogs.PauseDialog()
  
  //  Panel for the match status
  private val statusPanel = new BoxPanel(Orientation.Horizontal):
    contents ++= Seq(
      player1Label, Swing.HStrut(30),
      turnLabel, Swing.HStrut(30),
      player2Label, Swing.HStrut(40),
      pauseButton
    )
  listenTo(pauseButton)
  reactions += {
    case ButtonClicked(`pauseButton`) => pauseDialog.open()
  }
  
  // Panel for the game board
  private val boardPanel = new GridPanel(1, 1):
    background = GUIColor.Green()
  
  contents ++= Seq(
    statusPanel,
    boardPanel
  )
  
  def startMatch(): Unit =
    val (shape, color, difficulty) = mainFrame.settingsPanel.getSettings
    mainFrame.controller.startMatch(shape, color, difficulty)
  
  def update(matchState: MatchState): Unit =
    val board = matchState.board
    val disks = board.disks.map(state => state.position -> state.color).toMap
    val userDisks = disks.filter(_._2 == matchState.user.color)
    val opponentDisks = disks -- userDisks.keys
    
    // Update the labels with the current scores and turn
    player1Label.text = s"Player score: ${userDisks.size}"
    player2Label.text = s"Opponent score: ${opponentDisks.size}"
    turnLabel.text = matchState.activePlayer match
      case ActivePlayer.User => "Your turn"
      case _ => "Opponent's turn"
      
    // Update the board grid with the current disks and available moves
    val (gridWidth, gridHeight) = board.shape match
      case Shape.Square(size) => (size, size)
      case Shape.Rectangle(width, height) => (width, height)
    boardPanel.rows = gridWidth
    boardPanel.columns = gridHeight
    boardPanel.preferredSize = new Dimension(gridWidth * 50, gridHeight * 50)
    
    val boardGrid = for
      row <- 0 until gridWidth
      col <- 0 until gridHeight
    yield Position(row, col)   

    boardPanel.contents.clear()
    boardGrid.foreach(position =>
      val diskButton = disks.get(position) match
        case Some(color) =>
          DiskButton.PlacedDisk(color)
        case _ if board.userAvailablePlacements.contains(position) =>
          DiskButton.PlaceableDisk(matchState.user.color)
        case _ =>
          DiskButton.EmptyDisk
  
      val button = diskButton.button
      boardPanel.contents += button
      if diskButton == DiskButton.PlaceableDisk(matchState.user.color) then
        listenTo(button)
        button.action = Action("")(
          mainFrame.controller.handleSelection(position)
        )
    )
    
    // After updating the panel, check if the match is still in progress.
    // If the match has ended, open the MatchStatusDialog to show the result.
    if matchState.status != MatchStatus.InProgress then
      dialogs.MatchStatusDialog(matchState).open()
