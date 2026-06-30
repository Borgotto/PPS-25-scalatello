package it.unibo.pps.testutils

import it.unibo.pps.model.board.{Board, BoardImpl, Disk}
import it.unibo.pps.utils.{Color, Position, Shape}

object TestExtensions:

  extension (expr: String)

    def toBoard: Board =

      val lines = expr.stripMargin.trim.split("\n")
        .map(_.trim.replaceAll("\\s+", ""))
        .filter(_.nonEmpty)

      if (lines.isEmpty) throw IllegalArgumentException("Board string is empty")

      val rows = lines.length
      val cols = lines.head.length

      val isUniform = lines.forall(_.length == cols)
      if (!isUniform) throw IllegalArgumentException("Columns length in board string is not uniform")

      val shape = if rows == cols then Shape.Square(rows) else Shape.Rectangle(rows, cols)

      val disks = for {
        (line, row) <- lines.zipWithIndex
        (char, col) <- line.trim.replaceAll("\\s+", "").zipWithIndex
      } yield {
        char match
          case 'B' => Some(Disk(Color.Black, Position(row, col)))
          case 'W' => Some(Disk(Color.White, Position(row, col)))
          case '.' => None
          case _ => throw IllegalArgumentException(s"Invalid character $char in board string")
      }

      BoardImpl(shape, disks.flatten)
