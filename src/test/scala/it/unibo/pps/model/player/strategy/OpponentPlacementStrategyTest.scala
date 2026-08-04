package it.unibo.pps.model.player.strategy

import it.unibo.pps.domain.*
import it.unibo.pps.domain.MatchStatus.*
import it.unibo.pps.domain.OpponentType.*
import it.unibo.pps.model.Logic
import it.unibo.pps.model.board.Board
import it.unibo.pps.model.player.*
import it.unibo.pps.model.player.strategy.SmartPlacementStrategy
import it.unibo.pps.testutils.TestExtensions.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.prop.{TableDrivenPropertyChecks, TableFor3}
import org.scalatest.ParallelTestExecution

/** Test suite for opponent placement strategy. */
class OpponentPlacementStrategyTest extends AnyFlatSpec with TableDrivenPropertyChecks with ParallelTestExecution:

  "ErraticOpponentPlacementStrategy" should "return a valid placement" in:
    given board: Board = """
      ......
      ...W..
      ..BW..
      ..BW..
      ......
      """.toBoard
    val opponent = Opponent.ErraticOpponent(Color.White)
    val availablePlacements = board.getAvailablePlacements(opponent.color)
    val position: Position = opponent.strategy.computePlacement
    availablePlacements should contain (position)

  private val inputs: TableFor3[Board, Opponent, Long] = Table(
    ("board", "opponent", "expected time in ms"),
    (
      """
      ......
      ..BW..
      .WWB..
      .BBW..
      ......
      """.toBoard, Opponent.EasyOpponent(Color.Black), 250L
    ),
    (
      """
      ......
      ..BBBB
      ..WBB.
      ..W...
      ......
      ......
      """.toBoard, Opponent.MediumOpponent(Color.White), 500L
    ),
    (
      """
      ........
      ........
      ........
      ...BBWB.
      ..BBWB..
      ..BB....
      ..WBW...
      ....W...
      ........
      """.toBoard, Opponent.HardOpponent(Color.Black), 2500L
    )
  )

  behave like smartOpponentPerformance (using inputs)

  // Shared tests for SmartOpponentPlacementStrategy performance
  def smartOpponentPerformance(using inputs: TableFor3[Board, Opponent, Long]): Unit =
    forEvery(inputs): (b, opponent, expectedTime) =>
      given Board = b
      val availablePlacements = b.getAvailablePlacements(opponent.color)
      val testSubject = s"SmartOpponentPlacementStrategy" +
                        " (input #" + inputs.indexOf((b, opponent, expectedTime)) + ")"
      val depth = opponent.strategy match
        case SmartPlacementStrategy(_, d) => d
        case _ => 0
      
      testSubject should s"return a valid placement" in:
        val position: Position = opponent.strategy.computePlacement
        availablePlacements should contain (position)

      // todo(borghini): find a hardware independent way to test performance
      it should s"finish within ${expectedTime}ms for ${availablePlacements.size} available moves at depth ${depth}" in:
        val numberOfRuns = 3
        val totalTime = (1 to numberOfRuns).map( _ =>
          val startTime = System.currentTimeMillis()
          opponent.strategy.computePlacement
          val timeTaken = System.currentTimeMillis() - startTime
          timeTaken
        )
        val averageTime = totalTime.sum.toDouble / numberOfRuns
        assert(averageTime < expectedTime, s"SmartOpponentPlacementStrategy took too long on average: ${averageTime} ms")

      def playWholeMatch(player: Opponent)(using difficulty: OpponentType): Logic =
        val logic = Logic(b.state.shape, player.color, difficulty)
        Iterator.iterate(logic)((turn: Logic) =>
          turn.state.activePlayer match
            case ActivePlayer.Opponent => turn.placeOpponentDisk()
            case ActivePlayer.User =>
              given Board = Board(turn.state.board)
              val position = player.strategy.computePlacement
              turn.placeUserDisk(position)
        ).dropWhile(_.state.status == InProgress)
        .next()

      it should "win against an easier opponent" in:
        given difficulty: OpponentType = opponent match
          case Opponent.HardOpponent(_) => Medium
          case _ => Easy
        val matchEnd = playWholeMatch(opponent)
        matchEnd.state.status shouldBe UserWon

      it should "lose against a harder opponent" in:
        given difficulty: OpponentType = opponent match
          case Opponent.EasyOpponent(_) => Medium
          case _ => Hard
        val cappedOpponent: Opponent = difficulty match
          case Hard => Opponent.MediumOpponent(opponent.color)
          case _ => opponent
        val matchEnd = playWholeMatch(cappedOpponent)
        matchEnd.state.status shouldBe OpponentWon
