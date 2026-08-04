package it.unibo.pps.model.board

import it.unibo.pps.domain.{Color, Position}

/** This object contains helper extension methods of [[String]] to create a [[Board]]. */
object BoardCreationExtensions:
  extension (s: String)
    /** Transforms a string with a specific pattern in a 
     * [[scala.collection.immutable.Map]] of [[domain.Position]] -> [[Disk]].
     * 
     *  This is an extension method of [[String]].
     *
     *  The pattern to follow is: (x,y) -> c where:
     *    - x and y are any number
     *    - c is the color of the disk, the accepted ones are w or W for white and b or B for black.
     *
     *  @example {{{
     *            """
     *              (0,0) -> W
     *              (1,1) -> B
     *            """.toPosDiskMap
     *
     *            results in:
     *
     *            Map[Position, Disk](
     *              Position(0,0) -> Disk(Color.White)
     *              Position(1,1) -> Disk(Color.Black)
     *            ) }}}
     *            
     *  @return the [[scala.collection.immutable.Map]] created from the string.
     */
    def toPosDiskMap: Map[Position, Disk] =
      val pattern = """(\((?<position>\d+,\s*\d+)\)\s*->\s*(?<color>[A-Za-z]))""".r
      pattern.findAllMatchIn(s).map(w =>
        val position: Position = w.group("position")
        val disk = w.group("color").toLowerCase match
          case "w" => Disk(Color.White)
          case "b" => Disk(Color.Black)
        position -> disk
      ).toMap
