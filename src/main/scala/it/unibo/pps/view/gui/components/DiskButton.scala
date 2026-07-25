package it.unibo.pps.view.gui.components

import it.unibo.pps.domain.Color as DiskColor
import it.unibo.pps.view.gui.theme.GUIColor
import it.unibo.pps.view.gui.theme.GUIColor.given

import scala.swing.*
import java.awt.Color as JColor
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints

enum DiskButton:
  case PlacedDisk(diskColor: DiskColor)
  case PlaceableDisk(diskColor: DiskColor)
  case EmptyDisk

  def button: Button = new Button():
    opaque = false
    enabled = false
    contentAreaFilled = false
    border = Swing.LineBorder(GUIColor.Black(200), 1)
    enabled = this == PlaceableDisk
    peer.setRolloverEnabled(true)    

    // Custom painting of the button to draw a disk
    // with appropriate color and transparency based on its type
    override def paintComponent(g: Graphics2D): Unit =
      g.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )

      val padding = 4
      val diskWidth = size.width - (padding * 2)
      val diskHeight = size.height - (padding * 2)

      DiskButton.this match
        case PlacedDisk(color) =>
          g.setColor(color())
          g.fillOval(padding, padding, diskWidth, diskHeight)

        case PlaceableDisk(color) =>
          g.setColor(color.transparent(80))
          g.fillOval(padding, padding, diskWidth, diskHeight)
          val isHovered = peer.getModel.isRollover || peer.isFocusOwner
          if isHovered then
            g.setStroke(new BasicStroke(2))
            g.setColor(JColor.YELLOW)
            g.drawOval(padding, padding, diskWidth, diskHeight)

        case _ =>

      super.paintComponent(g)