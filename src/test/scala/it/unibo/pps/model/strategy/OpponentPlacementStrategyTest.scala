package it.unibo.pps.model.strategy

import it.unibo.pps.model.board.Board
import it.unibo.pps.model.player.Opponent
import it.unibo.pps.utils.{Color, Position}
import it.unibo.pps.testutils.TestExtensions.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{contain, should, shouldBe}
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor4}

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

  private val inputs: TableFor4[Board, Int, Long, Position] = Table(
    ("board", "depth", "expected time in ms", "expected position"),
    (
      """
      ..W.
      .WWB
      .BBB
      ....
      """.toBoard, 3, 100L, Position(3, 1)
    ),
    (
      """
      ......
      ..BB..
      .BWB..
      ..B...
      ......
      ......
      """.toBoard, 4, 350L, Position(0, 2)
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
      """.toBoard, 5, 2000L, Position(2, 1)
    )
  )

  behave like smartOpponentPerformance (using inputs)

  // Shared tests for SmartOpponentPlacementStrategy performance
  def smartOpponentPerformance(using inputs: TableFor4[Board, Int, Long, Position]): Unit =
    forEvery(inputs): (b, depth, expectedTime, expectedMove) =>
      given Board = b
      val availablePlacements = b.getAvailablePlacements(Color.White)
      val opponent = Opponent.SmartOpponent(Color.White, depth)
      val testSubject = s"SmartOpponentPlacementStrategy" +
                        " (input #" + inputs.indexOf((b, depth, expectedTime, expectedMove)) + ")"

      testSubject should s"return a valid placement" in:
        val position: Position = opponent.strategy.computePlacement
        availablePlacements should contain (position)

      // todo(borghini): replace this test with a simulation of a game between two SmartOpponent where the smartest should win
      it should "return an advantageous position" in:
        val position: Position = opponent.strategy.computePlacement
        position shouldBe expectedMove

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
        println(s"Average time taken: ${averageTime} ms")
        assert(averageTime < expectedTime, s"SmartOpponentPlacementStrategy took too long on average: ${averageTime} ms")