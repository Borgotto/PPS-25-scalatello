package it.unibo.pps.view.io

import it.unibo.pps.state.BoardState
import it.unibo.pps.utils.Color.{Black, White}
import it.unibo.pps.utils.Position

object BoardRenderingExtensions:

  extension (state: BoardState)

    def render(): String =
      val maxRowIndex = state.shape.maxRow
      val maxColumnIndex = state.shape.maxColumn
      val firstLine = renderFormattedFirstLine(maxColumnIndex)
      val rows = renderRows(maxRowIndex, maxColumnIndex)
      "\n".concat((firstLine +: rows).mkString("\n"))

    private def renderFormattedFirstLine(maxColumnIndex: Int): String =
      (0 to maxColumnIndex)
        .map(index => if index == 0 then "     0" else f"$index%4d")
        .mkString("")

    private def renderRows(maxRowIndex: Int, maxColumnIndex: Int): Seq[String] =
      for rowIndex <- 0 to maxRowIndex yield renderRow(rowIndex, maxColumnIndex)

    private def renderRow(rowIndex: Int, maxColumnIndex: Int): String =
      val cells = for columnIndex <- 0 to maxColumnIndex yield renderCell(rowIndex, columnIndex)
      formatRow(rowIndex, cells)

    private def renderCell(rowIndex: Int, columnIndex: Int): String =
      val position = Position(rowIndex, columnIndex)
      val diskInPosition = state.disks.find(_.position == position)
      diskInPosition match
        case Some(disk) if disk.color == Black => "B"
        case Some(disk) if disk.color == White => "W"
        case None if state.userAvailablePlacements.contains(position) => "*"
        case _ => " "

    private def formatRow(rowIndex: Int, cells: Seq[String]): String = f"$rowIndex%-2d | " + cells.mkString(" | ") + " |"
