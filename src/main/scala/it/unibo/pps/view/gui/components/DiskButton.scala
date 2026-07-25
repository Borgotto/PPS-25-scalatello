package it.unibo.pps.view.gui.components

import it.unibo.pps.domain.Color as DiskColor

import java.awt.Color as JColor
import it.unibo.pps.view.gui.theme.GUIColor

import scala.swing.{Color as SwingColor, *}
import scala.swing.event.*

enum DiskButton:
  case PlacedDisk(diskColor: DiskColor)
  case PlaceableDisk
  case EmptyDisk
  
  def button: Button =
    val btn = new Button()
    btn.enabled = false
    btn.border = Swing.LineBorder(GUIColor.Black(200), 1)
    this match
      case PlacedDisk(diskColor) =>
        btn.background = GUIColor.toSwingColor(diskColor)
      case EmptyDisk =>
        btn.contentAreaFilled = false
      case PlaceableDisk =>
        btn.background = GUIColor.Black(75)
        btn.border = Swing.LineBorder(JColor.YELLOW, 1)
        btn.enabled = true
        btn.opaque = false
        btn.listenTo(btn.mouse.moves, btn)
        btn.reactions += {
          case _: MouseEntered | _: FocusGained =>
            btn.opaque = true
            btn.background = GUIColor.Green().darker
          case _: MouseExited | _: FocusLost =>
            btn.opaque = false
            btn.background = GUIColor.Black(75)
        }
    btn