package it.unibo.pps.testutils

import it.unibo.pps.model.board.{Board, Disk}
import it.unibo.pps.utils.{Position, Shape}
import it.unibo.pps.utils.Color.*
import it.unibo.pps.utils.Shape.*

object TestExtensions:

  extension (string: String)

    def toBoard: Board =
      val lines = parseLines(string)
      checkIfEmpty(lines)
      val nRows = lines.length
      val nColumns = lines.head.length
      checkIfUniform(lines, nColumns)
      getBoard(lines, nRows, nColumns)

    private def parseLines(text: String): Seq[String] =
      text.stripMargin.trim.split("\n")
        .map(_.trim.replaceAll("\\s+", ""))
        .filter(_.nonEmpty)
        .toSeq

    private def checkIfEmpty(lines: Seq[String]): Unit =
      if lines.isEmpty then throw IllegalArgumentException("Board string is empty")

    private def checkIfUniform(lines: Seq[String], columns: Int): Unit =
      val isUniform = lines.forall(_.length == columns)
      if !isUniform then throw IllegalArgumentException("Columns length in board string is not uniform")

    private def getBoard(lines: Seq[String], nRows: Int, nColumns: Int): Board =
      val shape = getShape(nRows, nColumns)
      val disks = for
        (line, row) <- lines.zipWithIndex
        (char, column) <- line.trim.replaceAll("\\s+", "").zipWithIndex
      yield getDiskByChar(char, Position(row, column))
      Board(shape, disks.flatten.toMap)
    
    private def getShape(nRows: Int, nColumns: Int): Shape =
      if nRows == nColumns then Square(nRows) else Rectangle(nRows, nColumns)
    
    private def getDiskByChar(char: Char, position: Position): Option[(Position, Disk)] = char match
      case 'B' => Some(position -> Disk(Black))
      case 'W' => Some(position -> Disk(White))
      case '.' => None
      case _ => throw IllegalArgumentException(s"Invalid character $char in board string")
      