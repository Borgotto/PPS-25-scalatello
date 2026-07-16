package it.unibo.pps.model.board

import it.unibo.pps.utils.{Color, Position}

/** Contains helper `extension methods` to create a [[Board]] */
object BoardCreationExtensions:
  extension (s: String)
    /** Extension method of [[String]].
     *
     * Transforms a string with a specific pattern in a
     * [[scala.collection.immutable.Map]] of [[Position]] -> [[Disk]].
     *
     * The pattern to follow is: (x,y) -> c where:
     *    - x and y are any number
     *    - c is the color of the disk, the accepted ones are w or W for white and b or B for black.
     *
     * @example {{{
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
     * @return the [[scala.collection.immutable.Map]] created from the string
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
      
  extension (i: Int)
    /** Extension method of [[Int]]
     * @return the number divided by 2
     */
    def half: Int = i/2
