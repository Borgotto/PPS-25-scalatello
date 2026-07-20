package it.unibo.pps.model.strategy

import it.unibo.pps.domain.{ActivePlayer, Color, OpponentType, Position}
import it.unibo.pps.domain.MatchStatus.*
import it.unibo.pps.domain.OpponentType.*
import it.unibo.pps.domain.Shape.*
import it.unibo.pps.model.Logic
import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.IntExtensions.*
import it.unibo.pps.model.player.*
import it.unibo.pps.testutils.TestExtensions.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{contain, should, shouldBe}
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}

class OpponentPlacementStrategyTest extends AnyFlatSpec with TableDrivenPropertyChecks:

  "RandomOpponentPlacementStrategy" should "return a valid placement" in:
    given board: Board = """
      ....
      .WB.
      .BW.
      ....
      """.toBoard
    val opponent = Opponent.RandomOpponent(Color.White)
    val availablePlacements = board.getAvailablePlacements(opponent.color)
    val position: Position = opponent.strategy.computePlacement
    availablePlacements should contain (position)

  private val inputs: TableFor3[Board, Int, Long] = Table(
    ("board", "depth", "expected time in ms"),
    (
      """
      ..BW
      .WWB
      .BBB
      ....
      """.toBoard, 3, 100L
    ),
    (
      """
      ......
      ..BB..
      .BWBB.
      ..B...
      ......
      ......
      """.toBoard, 4, 350L
    ),
    (
      """
      ........
      ......W.
      ....BWB.
      ..BBWB..
      ..BBW...
      ..W.W...
      ....W...
      ........
      """.toBoard, 5, 2000L
    )
  )

  behave like smartOpponentPerformance (using inputs)

  // Shared tests for SmartOpponentPlacementStrategy performance
  def smartOpponentPerformance(using inputs: TableFor3[Board, Int, Long]): Unit =
    forEvery(inputs): (b, depth, expectedTime) =>
      given Board = b
      val availablePlacements = b.getAvailablePlacements(Color.White)
      val opponent = Opponent.SmartOpponent(Color.White, depth)
      val testSubject = s"SmartOpponentPlacementStrategy" +
                        " (input #" + inputs.indexOf((b, depth, expectedTime)) + ")"

      testSubject should s"return a valid placement" in:
        val position: Position = opponent.strategy.computePlacement
        availablePlacements should contain (position)

      // todo(borghini): find a hardware independent way to test performance
      it should s"finish within ${expectedTime}ms for ${availablePlacements.size} available moves at depth $depth" in:
        val numberOfRuns = 3
        val totalTime = (1 to numberOfRuns).map( _ =>
          val startTime = System.currentTimeMillis()
          opponent.strategy.computePlacement
          val timeTaken = System.currentTimeMillis() - startTime
          timeTaken
        )
        val averageTime = totalTime.sum.toDouble / numberOfRuns
        assert(averageTime < expectedTime, s"SmartOpponentPlacementStrategy took too long on average: ${averageTime} ms")

      def playWholeMatch(player: Player)(using difficulty: OpponentType): Logic =
        val boardShape = Square(8)
        val logic = Logic(boardShape, opponent.color, difficulty)
        Iterator.iterate(logic)((turn: Logic) =>
          turn.state.activePlayer match
            case ActivePlayer.Opponent => turn.placeOpponentDisk()
            case ActivePlayer.User =>
              given Board = Board(turn.state.board)
              val position = opponent.strategy.computePlacement
              turn.placeUserDisk(position)
        ).dropWhile(_.state.status == InProgress)
        .next()

      it should "win against an easier opponent" in:
        given difficulty: OpponentType = opponent.strategy match
          case SmartPlacementStrategy(_, depth) if depth.inRange(2, 3) => Easy
          case SmartPlacementStrategy(_, depth) if depth >= 4 => Medium
          case _ => Random
        val matchEnd = playWholeMatch(opponent)
        matchEnd.state.status shouldBe UserWon

      it should "lose against a harder opponent" in:
        given difficulty: OpponentType = opponent.strategy match
          case RandomPlacementStrategy(_) => Easy
          case SmartPlacementStrategy(_, depth) if depth <= 2 => Medium
          case _ => Hard
        val cappedOpponent = Opponent.SmartOpponent(opponent.color, math.max(depth, 4))
        val matchEnd = playWholeMatch(cappedOpponent)
        matchEnd.state.status shouldBe OpponentWon
