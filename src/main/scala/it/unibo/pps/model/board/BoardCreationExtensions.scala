package it.unibo.pps.model.board

import it.unibo.pps.utils.{Color, Position}

object BoardCreationExtensions:
  extension (s: String)
    def toPosDiskMap: Map[Position, Disk] =
      val pattern = """(\((?<position>\d+,\s*\d+)\)\s*->\s*(?<color>[A-Za-z]))""".r
      pattern.findAllMatchIn(s).map(w =>
        val position: Position = w.group("position")
        val disk = w.group("color").toLowerCase match
          case "w" => Disk(Color.White)
          case "b" => Disk(Color.Black)
        position -> disk
      ).toMap
      
  extension (i: Int)
    def half: Int = i/2
