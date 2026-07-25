package it.unibo.pps.view.gui.components.panels

import os.Path
import java.io.File
import javax.imageio.ImageIO
import scala.swing.{Graphics2D, Image, Panel}

trait ImagePanel(val filePath: Path) extends Panel:
  private val image: Image = ImageIO.read(new File(filePath.toString))
  override def paintComponent(g: Graphics2D): Unit =
    if image != null then
      val size = this.size
      val hPadding = (size.width - size.width/2) / 2
      val vPadding = (size.height - size.height/6) / 2
      val xSize = size.width/2
      val ySize = size.height/6
      g.drawImage(image, hPadding, vPadding, xSize, ySize, null)

object ImagePanel:
  def apply(path: Path): ImagePanel = new ImagePanel(path) {}