package it.unibo.pps.view.gui.theme

import it.unibo.pps.domain.Color as DiskColor
import java.awt.Color as JColor

import scala.swing.*
import java.awt.Color as JColor

enum GUIColor:
  case White
  case Black
  case Green  

  def apply(alpha: Int): JColor = this match
    case White => JColor(255, 255, 255, alpha)
    case Black => JColor(0, 0, 0, alpha)
    case Green => JColor(0, 134, 89, alpha)

  def apply(): JColor = this(255)
    
  def transparent(alpha: Int): JColor = this(alpha)
  
  def opaque: JColor = this(255)

object GUIColor:
  given toJColor: Conversion[GUIColor, JColor] = color => color()
  given toSwingColor: Conversion[GUIColor, scala.swing.Color] = color => color()
  given fromJcolor: Conversion[JColor, GUIColor] = color => {
    val (r, g, b) = (color.getRed, color.getGreen, color.getBlue)
    (r, g, b) match
      case (255, 255, 255) => GUIColor.White
      case (0, 0, 0) => GUIColor.Black
      case (0, 134, 89) => GUIColor.Green
      case _ => throw new IllegalArgumentException(s"Unknown color: $color")
  }  
  given Conversion[DiskColor, GUIColor] = {
    case DiskColor.White => GUIColor.White
    case DiskColor.Black => GUIColor.Black
  }

  extension (color: JColor)
    def transparent(alpha: Int): JColor =
      JColor(color.getRed, color.getGreen, color.getBlue, alpha)

    def opaque: JColor =
      JColor(color.getRed, color.getGreen, color.getBlue)
