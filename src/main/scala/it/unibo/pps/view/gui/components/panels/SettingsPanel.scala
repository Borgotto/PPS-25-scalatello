package it.unibo.pps.view.gui.components.panels

import it.unibo.pps.view.components.*
import it.unibo.pps.view.gui.components.Panels.*
import it.unibo.pps.view.View
import it.unibo.pps.domain.{Color, OpponentType, Shape}
import it.unibo.pps.model.player.Opponent
import it.unibo.pps.view.gui.components.ApplicationFrame

import scala.swing.*
import scala.swing.event.*

/**
 * A panel that shows a list of settings that can be changed by the user.
 * Available settings are:
 * - A radio button to choose the user's disk color: Black(default) or White
 * - A dropdown to choose the board shape: Square(default) or Rectangular
 * - A slider to choose the board size: 4-16 (default 8)
 * - A slider to choose the opponent's difficulty: Easy, Medium(default), Hard
 * - A button to return to the main menu
 */
class SettingsPanel
    (using mainFrame: ApplicationFrame)
    extends BoxPanel(Orientation.Vertical):

  mainFrame.title = "Scalatello - Settings"

  private val colorLabel = new Label("Choose your disk color:")
  private val colorBlack = new RadioButton("Black")
  private val colorWhite = new RadioButton("White")
  private val colorGroup = new ButtonGroup(colorBlack, colorWhite)
  colorBlack.selected = true

  private val shapeLabel = new Label("Choose the board shape:")
  private val shapeSquare = new RadioButton("Square")
  private val shapeRectangular = new RadioButton("Rectangular")
  private val shapeGroup = new ButtonGroup(shapeSquare, shapeRectangular)
  shapeSquare.selected = true

  private val sizeLabel = new Label("Choose the board size:")
  private val sizeSlider = new Slider()
  sizeSlider.min = 4
  sizeSlider.max = 16
  sizeSlider.value = 8
  sizeSlider.majorTickSpacing = 2
  sizeSlider.snapToTicks = true
  sizeSlider.paintTicks = true
  sizeSlider.paintLabels = true

  private val difficultyLabel = new Label("Choose the opponent's difficulty:")
  private val difficultyDropdown = new ComboBox(Seq("Random", "Easy", "Medium", "Hard"))
  difficultyDropdown.selection.index = 2 // Default to Medium

  private val btnBack = new Button("Back to Main Menu")
  listenTo(btnBack)

  reactions += {
    case ButtonClicked(`btnBack`) => mainFrame.navigateTo(MainMenu)
  }
  contents ++=
    colorLabel :: colorBlack :: colorWhite :: Swing.VStrut(20) ::
    shapeLabel :: shapeSquare :: shapeRectangular :: Swing.VStrut(20) ::
    sizeLabel :: sizeSlider :: Swing.VStrut(20) ::
    difficultyLabel :: difficultyDropdown :: Swing.VStrut(20) ::
    btnBack :: Nil
  
  def getSettings: (Shape, Color, OpponentType) =
    val color = if (colorBlack.selected) Color.Black else Color.White
    val size = sizeSlider.value
    val shape = if (shapeSquare.selected) Shape.Square(size) else Shape.Rectangle(size, size * 2)
    val opponent: OpponentType = difficultyDropdown.selection.index match
      case 0 => OpponentType.Random
      case 1 => OpponentType.Easy
      case 2 => OpponentType.Medium
      case 3 => OpponentType.Hard
    (shape, color, opponent)