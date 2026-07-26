package it.unibo.pps.view.cli.io

import it.unibo.pps.state.BoardState
import it.unibo.pps.domain.Color.{Black, White}
import it.unibo.pps.domain.Position

/** Provides utilities for the rendering of the board. */
object BoardRendering:

  extension (state: BoardState)

    /** @return a string representation of the board state. */
    def render(): String =
      val firstLine = renderFormattedFirstLine()
      val rows = renderRows()
      val separator = "\n" + "-" * firstLine.length
      s"\n$firstLine" + rows.mkString(separator)

    private def renderFormattedFirstLine(): String =
      (0 to state.shape.maxColumnIndex)
        .map(index => if index == 0 then "     0" else f"$index%4d")
        .mkString("")
        .concat("  ")

    private def renderRows(): Seq[String] =
      for rowIndex <- 0 to state.shape.maxRowIndex yield renderRow(rowIndex)

    private def renderRow(rowIndex: Int): String =
      val cells = for columnIndex <- 0 to state.shape.maxColumnIndex yield renderCell(rowIndex, columnIndex)
      formatRow(rowIndex, cells)

    private def renderCell(rowIndex: Int, columnIndex: Int): String =
      val position = Position(rowIndex, columnIndex)
      val diskInPosition = state.disks.find(_.position == position)
      diskInPosition match
        case Some(disk) if disk.color == Black => "B"
        case Some(disk) if disk.color == White => "W"
        case None if state.userAvailablePlacements.contains(position) => "*"
        case _ => " "

    private def formatRow(rowIndex: Int, cells: Seq[String]): String = 
      f"\n$rowIndex%-2d | " + cells.mkString(" | ") + " |"
