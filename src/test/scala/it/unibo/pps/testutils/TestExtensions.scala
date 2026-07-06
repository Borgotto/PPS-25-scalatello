package it.unibo.pps.testutils

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.utils.Position
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.Shape.*

object TestExtensions:

  extension (expr: String)

    def toBoard: Board =

      val lines = expr.stripMargin.trim.split("\n")
        .map(_.trim.replaceAll("\\s+", ""))
        .filter(_.nonEmpty)
        .toIndexedSeq

      if lines.isEmpty then throw IllegalArgumentException("Board string is empty")

      val rows = lines.length
      val cols = lines.head.length

      val isUniform = lines.forall(_.length == cols)
      if !isUniform then throw IllegalArgumentException("Columns length in board string is not uniform")

      val shape = if rows == cols then Square(rows) else Rectangle(rows, cols)

      val disks = for {
        (line, row) <- lines.zipWithIndex
        (char, col) <- line.trim.replaceAll("\\s+", "").zipWithIndex
      } yield {
        char match
          case 'B' => Some(Position(row, col) -> Disk(Black))
          case 'W' => Some(Position(row, col) -> Disk(White))
          case '.' => None
          case _ => throw IllegalArgumentException(s"Invalid character $char in board string")
      }

      Board(shape, disks.flatten.toMap)
